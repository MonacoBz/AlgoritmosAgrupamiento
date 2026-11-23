const CONFIG = {
    PREPARE_URL: 'http://localhost:8080/api/gateway/prepare',
    CLEAN_URL: 'http://localhost:8080/api/gateway/clean',
    RESOLVE_URL: 'http://localhost:8080/api/gateway/resolve'
};

let currentMatrixMeta = {};
let totalColumns = 0;

// --- HELPER: Reconstruye el objeto Matrix leyendo la tabla actual ---
function buildMatrixFromTable() {
    const matrix = {
        binaryData: [],
        qualitativeData: [],
        quantitativeData: [],
        rowsNum: currentMatrixMeta.rowsNum,
        totalVariables: currentMatrixMeta.totalVariables,
        repetitiveValues: currentMatrixMeta.repetitiveValues || []
    };

    for (let c = 0; c < totalColumns; c++) {
        const selectedType = document.getElementById(`col-type-${c}`).value;
        const colData = [];

        for (let r = 0; r < currentMatrixMeta.rowsNum; r++) {
            const val = document.getElementById(`cell-${r}-${c}`).value;
            colData.push(val === '' ? null : Number(val));
        }

        if (selectedType === 'BINARY') matrix.binaryData.push({ data: colData });
        else if (selectedType === 'QUALITATIVE') matrix.qualitativeData.push({ data: colData });
        else if (selectedType === 'QUANTITATIVE') matrix.quantitativeData.push({ data: colData });
    }

    return matrix;
}

// --- PASO 1: SUBIR Y PREPARAR (/prepare) ---
document.getElementById('uploadForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    const fileInput = document.getElementById('fileInput');
    if (fileInput.files.length === 0) return showError('Selecciona un archivo.');

    const formData = new FormData();
    formData.append('File', fileInput.files[0]);

    document.getElementById('error-message').classList.add('hidden');
    document.getElementById('loading').classList.remove('hidden');

    try {
        const response = await fetch(CONFIG.PREPARE_URL, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            const errDto = await response.json().catch(() => ({}));
            throw new Error(errDto.message || 'Error procesando el archivo.');
        }

        const reportDto = await response.json();
        renderCleaningReport(reportDto.report);
        renderMatrixEditor(reportDto.matrix);

        document.getElementById('clustering-controls').classList.add('hidden');
    } catch (error) {
        showError(error.message);
    } finally {
        document.getElementById('loading').classList.add('hidden');
    }
});

// --- PASO 2: LIMPIAR MATRIZ (/clean) ---
document.getElementById('cleanBtn').addEventListener('click', async function () {
    const matrixToClean = buildMatrixFromTable();
    document.getElementById('clean-loading').classList.remove('hidden');

    try {
        const response = await fetch(CONFIG.CLEAN_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(matrixToClean)
        });

        if (!response.ok) {
            const errDto = await response.json().catch(() => ({}));
            throw new Error(errDto.message || 'Error al limpiar la matriz.');
        }

        const reportDto = await response.json();
        renderCleaningReport(reportDto.report);
        renderMatrixEditor(reportDto.matrix);

        document.getElementById('clustering-controls').classList.remove('hidden');
    } catch (error) {
        alert('⚠️ Error en limpieza:\n\n' + error.message);
    } finally {
        document.getElementById('clean-loading').classList.add('hidden');
    }
});

// --- PASO 3: RESOLVER DISTANCIAS Y AGRUPAMIENTO (/resolve) ---
document.getElementById('calculateBtn').addEventListener('click', async function () {
    const cleanedMatrix = buildMatrixFromTable();
    const distanceMethod = document.getElementById('distanceMethod').value;
    const linkageMethod = document.getElementById('linkageMethod').value;

    const payload = {
        m: cleanedMatrix,
        method: linkageMethod,
        resolver: distanceMethod
    };

    document.getElementById('calc-loading').classList.remove('hidden');

    try {
        const response = await fetch(CONFIG.RESOLVE_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const errDto = await response.json().catch(() => ({}));
            throw new Error(errDto.message || 'Error al calcular el agrupamiento.');
        }

        const data = await response.json();
        renderResults(data, distanceMethod);
    } catch (error) {
        alert('⚠️ Validación detenida:\n\n' + error.message);
    } finally {
        document.getElementById('calc-loading').classList.add('hidden');
    }
});

// --- REINICIAR FLUJO ---
document.getElementById('resetBtn').addEventListener('click', () => {
    document.getElementById('upload-section').classList.remove('hidden');
    document.getElementById('editor-section').classList.add('hidden');
    document.getElementById('results-section').classList.add('hidden');
    document.getElementById('clustering-controls').classList.add('hidden');
    document.getElementById('error-message').classList.add('hidden');
    document.getElementById('fileInput').value = '';
});

// --- RENDER: HISTORIAL DE ERRORES Y TRANSFORMACIONES ---
function renderCleaningReport(report) {
    const box = document.getElementById('cleaning-report-box');
    const list = document.getElementById('cleaning-report-list');
    list.innerHTML = '';

    if (Array.isArray(report) && report.length > 0) {
        report.forEach(msg => {
            const li = document.createElement('li');
            li.textContent = msg;
            list.appendChild(li);
        });
    } else {
        const li = document.createElement('li');
        li.textContent = 'Sin errores ni transformaciones registradas.';
        list.appendChild(li);
    }

    box.classList.remove('hidden');
}

// --- RENDER: EDITOR DE MATRIZ Y VALORES REPETIDOS ---
function renderMatrixEditor(matrix) {
    currentMatrixMeta = {
        rowsNum: matrix.rowsNum,
        totalVariables: matrix.totalVariables,
        repetitiveValues: matrix.repetitiveValues || []
    };

    document.getElementById('editorRowsNum').textContent = matrix.rowsNum;
    document.getElementById('editorTotalVars').textContent = matrix.totalVariables;

    const repContainer = document.getElementById('repetitiveVars');
    const repetidos = matrix.repetitiveValues || [];
    if (repetidos.length > 0) {
        repContainer.innerHTML = repetidos.map(v => `<span class="badge">${v}</span>`).join('');
    } else {
        repContainer.innerHTML = '<span>Ninguno</span>';
    }

    const columnsDef = [];
    if (matrix.quantitativeData) {
        matrix.quantitativeData.forEach(c => columnsDef.push({ type: 'QUANTITATIVE', data: c.data }));
    }
    if (matrix.binaryData) {
        matrix.binaryData.forEach(c => columnsDef.push({ type: 'BINARY', data: c.data }));
    }
    if (matrix.qualitativeData) {
        matrix.qualitativeData.forEach(c => columnsDef.push({ type: 'QUALITATIVE', data: c.data }));
    }

    totalColumns = columnsDef.length;

    const thead = document.getElementById('editor-headers');
    const tbody = document.getElementById('editor-body');
    thead.innerHTML = '<th>Elemento</th>';
    tbody.innerHTML = '';

    columnsDef.forEach((col, index) => {
        const th = document.createElement('th');
        const select = document.createElement('select');
        select.id = `col-type-${index}`;
        select.className = 'type-select';
        select.innerHTML = `
            <option value="QUANTITATIVE">Cuantitativo</option>
            <option value="BINARY">Binario</option>
            <option value="QUALITATIVE">Cualitativo</option>
        `;
        select.value = col.type;
        th.appendChild(select);
        thead.appendChild(th);
    });

    for (let r = 0; r < matrix.rowsNum; r++) {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td><strong>E${r + 1}</strong></td>`;

        columnsDef.forEach((col, cIndex) => {
            const td = document.createElement('td');
            const val = (col.data[r] !== null && col.data[r] !== undefined) ? col.data[r] : '';
            td.innerHTML = `<input type="number" step="any" id="cell-${r}-${cIndex}" class="cell-input" value="${val}">`;
            tr.appendChild(td);
        });

        tbody.appendChild(tr);
    }

    document.getElementById('editor-section').classList.remove('hidden');
    document.getElementById('upload-section').classList.add('hidden');
    document.getElementById('results-section').classList.add('hidden');
}

let currentBinaryData = null;

function renderResults(data, distanceMethod) {
    const binaryTabs = document.getElementById('binary-tabs');

    if (data.jaccardAndSockal) {
        currentBinaryData = data.jaccardAndSockal;
        binaryTabs.classList.remove('hidden');

        document.getElementById('usedDistance').textContent = 'BINARY (Jaccard / Sokal)';
        document.getElementById('usedLinkage').textContent = currentBinaryData.linkageMethod;

        setActiveBinaryTab('jaccard');
        renderClusteringResult(currentBinaryData.jaccard);
    } else if (data.clusteringResponse) {
        currentBinaryData = null;
        binaryTabs.classList.add('hidden');

        document.getElementById('usedDistance').textContent = distanceMethod;
        document.getElementById('usedLinkage').textContent = data.clusteringResponse.linkageMethod;

        renderClusteringResult(data.clusteringResponse.result);
    } else {
        throw new Error('La respuesta del servidor no contiene resultados de agrupamiento.');
    }

    document.getElementById('results-section').classList.remove('hidden');
    document.getElementById('results-section').scrollIntoView({ behavior: 'smooth' });
}

function renderClusteringResult(res) {
    document.getElementById('rowsNum').textContent = res.distanceMatrix.length;
    renderDistanceMatrix(res.distanceMatrix);
    renderSortedRelations(res.sortedRelations);
    renderClusteringHistory(res.clusteringHistory);
    renderDendrogram(res.clusteringHistory, res.distanceMatrix.length);
}

function setActiveBinaryTab(type) {
    document.getElementById('tab-jaccard').classList.toggle('active', type === 'jaccard');
    document.getElementById('tab-sokal').classList.toggle('active', type === 'sokal');
}

document.getElementById('tab-jaccard').addEventListener('click', () => {
    if (!currentBinaryData) return;
    setActiveBinaryTab('jaccard');
    renderClusteringResult(currentBinaryData.jaccard);
});

document.getElementById('tab-sokal').addEventListener('click', () => {
    if (!currentBinaryData) return;
    setActiveBinaryTab('sokal');
    renderClusteringResult(currentBinaryData.sokal);
});

function renderDistanceMatrix(matrix) {
    const thead = document.querySelector('#distanceMatrixTable thead');
    const tbody = document.querySelector('#distanceMatrixTable tbody');
    const n = matrix.length;

    let headerHtml = '<tr><th></th>';
    for (let i = 0; i < n; i++) headerHtml += `<th>E${i + 1}</th>`;
    headerHtml += '</tr>';
    thead.innerHTML = headerHtml;

    tbody.innerHTML = '';
    matrix.forEach((row, i) => {
        const tr = document.createElement('tr');
        let cells = `<th>E${i + 1}</th>`;
        row.forEach(val => {
            cells += `<td>${Number(val).toFixed(4)}</td>`;
        });
        tr.innerHTML = cells;
        tbody.appendChild(tr);
    });
}

function renderSortedRelations(relations) {
    const tbody = document.querySelector('#relationsTable tbody');
    tbody.innerHTML = '';

    relations.forEach((rel, idx) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${idx + 1}</td>
            <td>E${rel.elementA}</td>
            <td>E${rel.elementB}</td>
            <td>${Number(rel.distance).toFixed(6)}</td>
        `;
        tbody.appendChild(tr);
    });
}

function renderClusteringHistory(history) {
    const tbody = document.querySelector('#historyTable tbody');
    tbody.innerHTML = '';

    history.forEach(step => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${step.step}</td>
            <td>${step.clusterALabel}</td>
            <td>${step.clusterBLabel}</td>
            <td><strong>${step.newClusterLabel}</strong></td>
            <td>${Number(step.distance).toFixed(6)}</td>
            <td>${step.size}</td>
        `;
        tbody.appendChild(tr);
    });
}

// --- RENDER: DENDROGRAMA SVG ---
function renderDendrogram(history, numLeaves) {
    const svg = document.getElementById('dendrogramSvg');
    svg.innerHTML = '';
    if (!history || history.length === 0) return;

    const width = 850;
    const height = 420;
    const margin = { top: 35, right: 40, bottom: 55, left: 70 };
    const plotW = width - margin.left - margin.right;
    const plotH = height - margin.top - margin.bottom;

    const nodeMap = new Map();
    for (let i = 0; i < numLeaves; i++) {
        nodeMap.set(i, { id: i, label: `E${i + 1}`, isLeaf: true });
    }
    history.forEach(s => {
        nodeMap.set(s.newClusterId, {
            id: s.newClusterId,
            label: s.newClusterLabel,
            left: s.clusterAId,
            right: s.clusterBId,
            distance: s.distance,
            isLeaf: false
        });
    });

    const rootId = history[history.length - 1].newClusterId;
    const leafOrder = [];
    (function dfs(id) {
        const node = nodeMap.get(id);
        if (!node) return;
        if (node.isLeaf) leafOrder.push(id);
        else {
            dfs(node.left);
            dfs(node.right);
        }
    })(rootId);

    const maxDist = Math.max(...history.map(s => s.distance), 1e-6);
    const yScale = d => margin.top + plotH - (d / (maxDist * 1.1)) * plotH;
    const xStep = plotW / Math.max(leafOrder.length - 1, 1);

    const coords = new Map();
    leafOrder.forEach((leafId, idx) => {
        coords.set(leafId, { x: margin.left + idx * xStep, y: yScale(0) });
    });

    const ns = 'http://www.w3.org/2000/svg';
    const addEl = (tag, attrs, text) => {
        const el = document.createElementNS(ns, tag);
        Object.entries(attrs).forEach(([k, v]) => el.setAttribute(k, v));
        if (text !== undefined) el.textContent = text;
        svg.appendChild(el);
    };

    // Eje Y
    addEl('line', {
        x1: margin.left - 15, y1: margin.top,
        x2: margin.left - 15, y2: yScale(0),
        stroke: '#94A3B8', 'stroke-width': 1.5
    });
    for (let i = 0; i <= 5; i++) {
        const val = (maxDist * 1.1 / 5) * i;
        const y = yScale(val);
        addEl('line', { x1: margin.left - 20, y1: y, x2: margin.left - 15, y2: y, stroke: '#94A3B8' });
        addEl('text', {
            x: margin.left - 25, y: y + 4,
            'text-anchor': 'end', 'font-size': 11, fill: '#475569'
        }, val.toFixed(2));
    }

    // Ramas
    history.forEach(s => {
        const a = coords.get(s.clusterAId);
        const b = coords.get(s.clusterBId);
        const mergeY = yScale(s.distance);
        const mergeX = (a.x + b.x) / 2;

        addEl('line', { x1: a.x, y1: a.y, x2: a.x, y2: mergeY, stroke: '#4A90E2', 'stroke-width': 2.2 });
        addEl('line', { x1: b.x, y1: b.y, x2: b.x, y2: mergeY, stroke: '#4A90E2', 'stroke-width': 2.2 });
        addEl('line', { x1: a.x, y1: mergeY, x2: b.x, y2: mergeY, stroke: '#4A90E2', 'stroke-width': 2.2 });

        addEl('text', {
            x: mergeX, y: mergeY - 6,
            'text-anchor': 'middle', 'font-size': 11, 'font-weight': 'bold', fill: '#1E293B'
        }, `${s.newClusterLabel} (${Number(s.distance).toFixed(2)})`);

        coords.set(s.newClusterId, { x: mergeX, y: mergeY });
    });

    // Etiquetas de hojas
    leafOrder.forEach(leafId => {
        const pos = coords.get(leafId);
        addEl('circle', { cx: pos.x, cy: pos.y, r: 3.5, fill: '#1E293B' });
        addEl('text', {
            x: pos.x, y: pos.y + 22,
            'text-anchor': 'middle', 'font-size': 13, 'font-weight': 'bold', fill: '#1E293B'
        }, `E${leafId + 1}`);
    });
}

function showError(msg) {
    const err = document.getElementById('error-message');
    err.textContent = msg;
    err.classList.remove('hidden');
}