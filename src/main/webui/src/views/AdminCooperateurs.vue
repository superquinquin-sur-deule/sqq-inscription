<template>
  <main class="container">
    <header class="header">
      <div class="brand">
        <img src="/superquinquin_logo_deule.svg" alt="SuperQuinquin sur Deûle" class="logo" />
        <div class="brand-text">
          <h1>Coopérateurs</h1>
          <p class="subtitle">Administration — liste des coopérateurs enregistrés</p>
        </div>
      </div>
    </header>

    <section class="section">
      <div class="toolbar">
        <input
          v-model="query"
          type="search"
          placeholder="Rechercher (tous champs)"
          class="search"
        />
        <span class="meta" v-if="!loading && !error">{{ filteredRows.length }} résultat(s)</span>
      </div>

      <div v-if="loading" class="state">Chargement…</div>
      <div v-else-if="error" class="state error">{{ error }}</div>

      <div v-else class="table-wrapper">
        <table class="table">
          <thead>
            <tr>
              <th v-for="col in columns" :key="col.key" @click="toggleSort(col.key)" :class="['th', sortableClass(col.key)]">
                <span>{{ col.label }}</span>
                <span class="sort-icon" aria-hidden="true" v-if="sortKey === col.key">
                  {{ sortDir === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in sortedRows" :key="row.id">
              <td>{{ row.id }}</td>
              <td>{{ format(row.genre) }}</td>
              <td>{{ row.prenom }}</td>
              <td>{{ row.nom }}</td>
              <td>{{ row.telephone }}</td>
              <td>{{ row.email }}</td>
              <td>{{ row.adresse }}</td>
              <td>{{ row.ville }}</td>
              <td>{{ row.codePostal }}</td>
              <td>{{ yesNo(row.etudiantOuMinimasSociaux) }}</td>
              <td>{{ row.nombreDePersonnesDansLeFoyer }}</td>
              <td>{{ row.parts }}</td>
              <td>{{ row.partsDeSoutien }}</td>
              <td>{{ yesNo(row.acceptationDesStatus) }}</td>
              <td>
                <span class="status" :class="statusClass(row.status)">{{ format(row.status) }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
  
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import type { CooperateurDTO } from '../api/model'
import { getSqqInscriptionAPI } from '../api/service/catalog'
import type { CooperateurStatus } from '../api/model'

type SortDir = 'asc' | 'desc'

const api = getSqqInscriptionAPI()
const rows = ref<CooperateurDTO[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const query = ref('')

// columns definition
const columns = [
  { key: 'id', label: 'ID' },
  { key: 'genre', label: 'Genre' },
  { key: 'prenom', label: 'Prénom' },
  { key: 'nom', label: 'Nom' },
  { key: 'telephone', label: 'Téléphone' },
  { key: 'email', label: 'Email' },
  { key: 'adresse', label: 'Adresse' },
  { key: 'ville', label: 'Ville' },
  { key: 'codePostal', label: 'Code postal' },
  { key: 'etudiantOuMinimasSociaux', label: 'Tarif réduit' },
  { key: 'nombreDePersonnesDansLeFoyer', label: 'Pers. foyer' },
  { key: 'parts', label: 'Parts' },
  { key: 'partsDeSoutien', label: 'Parts de soutien' },
  { key: 'acceptationDesStatus', label: 'Statuts acceptés' },
  { key: 'status', label: 'Statut' },
] as const

type ColumnKey = typeof columns[number]['key']

const sortKey = ref<ColumnKey>('id')
const sortDir = ref<SortDir>('asc')

function sortableClass(key: ColumnKey) {
  return sortKey.value === key ? (sortDir.value === 'asc' ? 'sorted-asc' : 'sorted-desc') : 'sortable'
}

function toggleSort(key: ColumnKey) {
  if (sortKey.value === key) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKey.value = key
    sortDir.value = 'asc'
  }
}

function safeString(v: unknown): string {
  if (v === null || v === undefined) return ''
  if (typeof v === 'boolean') return v ? 'true' : 'false'
  return String(v)
}

function yesNo(v?: boolean) {
  return v ? 'Oui' : 'Non'
}

function format(v: unknown) {
  return safeString(v)
}

function statusClass(v?: CooperateurStatus) {
  switch (v) {
    case 'PAYMENT_PENDING':
      return 'pending'
    case 'PAID':
      return 'paid'
    case 'PROCESSED':
      return 'processed'
    default:
      return 'unknown'
  }
}

const filteredRows = computed(() => {
  const q = query.value.trim().toLowerCase()
  if (!q) return rows.value
  return rows.value.filter((r) => {
    const values = [
      r.id,
      r.genre,
      r.prenom,
      r.nom,
      r.telephone,
      r.email,
      r.adresse,
      r.ville,
      r.codePostal,
      r.etudiantOuMinimasSociaux,
      r.nombreDePersonnesDansLeFoyer,
      r.parts,
      r.partsDeSoutien,
      r.acceptationDesStatus,
      r.status,
    ]
      .map(safeString)
      .join(' ') // space separated tokens
      .toLowerCase()
    return values.includes(q)
  })
})

const sortedRows = computed(() => {
  const key = sortKey.value
  const dir = sortDir.value
  const arr = [...filteredRows.value]
  arr.sort((a: any, b: any) => {
    const va = a?.[key]
    const vb = b?.[key]
    const sa = safeString(va)
    const sb = safeString(vb)
    if (sa < sb) return dir === 'asc' ? -1 : 1
    if (sa > sb) return dir === 'asc' ? 1 : -1
    return 0
  })
  return arr
})

onMounted(async () => {
  try {
    loading.value = true
    const resp = await api.getApiV1AdministrationCooperateurs()
    // The generated client returns AxiosResponse by default; unwrap data if present
    const data = (resp as any).data ?? (resp as any)
    rows.value = Array.isArray(data) ? data : []
  } catch (e: any) {
    error.value = e?.message ?? 'Une erreur est survenue lors du chargement.'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.container {
  background: #efefee;
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 2rem 1rem;
  color: #1f2937;
  border-radius: 0;
}
.header {
  padding: 1rem 1.25rem;
  margin-bottom: 1rem;
}
.brand { display: flex; align-items: center; gap: 1rem; }
.logo { height: 72px; }
.subtitle { color: #6b7280; margin: 0; }

.section { background: #fff; border: 1px solid #e6e8ee; padding: 1rem; border-radius: 8px; }
.toolbar { display: flex; align-items: center; gap: .75rem; margin-bottom: .75rem; }
.search { flex: 1; padding: .5rem .75rem; border: 1px solid #e5e7eb; border-radius: 8px; }
.meta { color: #6b7280; font-size: .9rem; }
.state { padding: 1rem; color: #374151; }
.state.error { color: #b91c1c; }

.table-wrapper { overflow: auto; }
.table { width: 100%; border-collapse: collapse; }
.table thead th { position: sticky; top: 0; background: #fafafa; }
.th { text-align: left; padding: .5rem .5rem; border-bottom: 1px solid #e5e7eb; cursor: pointer; user-select: none; white-space: nowrap; }
td { padding: .5rem .5rem; border-bottom: 1px solid #f3f4f6; }
.th.sortable:hover { background: #f9fafb; }
.sorted-asc, .sorted-desc { background: #f9fafb; }
.sort-icon { margin-left: .35rem; font-size: .75rem; color: #6b7280; }

/* Status badges */
.status {
  display: inline-block;
  padding: .15rem .5rem;
  border-radius: 999px;
  font-size: .85rem;
  font-weight: 600;
  line-height: 1.2;
}
.status.pending { /* PAYMENT_PENDING → orange */
  background: #ffedd5; /* orange-100 */
  color: #9a3412;     /* orange-700 */
}
.status.paid { /* PAID → yellow */
  background: #fef9c3; /* yellow-100 */
  color: #854d0e;      /* yellow-800 */
}
.status.processed { /* PROCESSED → green */
  background: #dcfce7; /* green-100 */
  color: #166534;      /* green-700 */
}
.status.unknown {
  background: #e5e7eb; /* gray-200 */
  color: #374151;      /* gray-700 */
}
</style>
