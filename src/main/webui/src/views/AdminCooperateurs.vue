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

    <div class="tabs">
      <button
        class="tab"
        :class="{ active: activeTab === 'inscriptions' }"
        @click="activeTab = 'inscriptions'"
      >
        Souscriptions
        <span class="tab-count">{{ totalElements }}</span>
      </button>
      <button
        class="tab"
        :class="{ active: activeTab === 'supplementaires' }"
        @click="activeTab = 'supplementaires'"
      >
        Parts supplémentaires
        <span class="tab-count">{{ totalElementsSupp }}</span>
      </button>
    </div>

    <section class="section" v-show="activeTab === 'inscriptions'">
      <div class="toolbar">
        <input
          v-model="query"
          type="search"
          placeholder="Rechercher (nom, prénom, email)"
          class="search"
        />
        <div class="status-filter">
          <span class="filter-label">Statut:</span>
          <label v-for="status in allStatuses" :key="status" class="status-checkbox">
            <input
              type="checkbox"
              :value="status"
              v-model="selectedStatuses"
              @change="loadCooperateurs(0, pageSize)"
            />
            <span class="status" :class="statusClass(status)">{{ format(status) }}</span>
          </label>
        </div>
        <span class="meta" v-if="!loading && !error">{{ totalElements }} résultat(s)</span>
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
              <td>{{ row.email }}</td>
              <td>{{ row.telephone }}</td>
              <td>{{ row.adresse }}</td>
              <td>{{ row.codePostal }}</td>
              <td>{{ row.ville }}</td>
              <td>{{ yesNo(row.etudiantOuMinimasSociaux) }}</td>
              <td>{{ row.nombreDePersonnesDansLeFoyer }}</td>
              <td>{{ row.parts }}</td>
              <td>{{ row.partsDeSoutien }}</td>
              <td>{{ yesNo(row.acceptationDesStatus) }}</td>
              <td>
                <button
                    v-if="row.binome"
                    class="binome-btn"
                    @click="openBinomeModal(row.binome)"
                >
                  Oui
                </button>
                <span v-else class="no-binome">Non</span>
              </td>
              <td>
                <span class="status" :class="statusClass(row.status)">{{ format(row.status) }}</span>
              </td>
              <td>{{ formatDate(row.createdAt) }}</td>
              <td>{{ formatDate(row.updatedAt) }}</td>
              <td>
                <button
                  v-if="row.status === 'PAYMENT_PENDING'"
                  class="copy-btn"
                  @click="copyRetryLink(row)"
                >
                  {{ copiedId === row.id ? 'Copie !' : 'Copier lien' }}
                </button>
                <button
                  v-if="row.status === 'PAYMENT_PENDING'"
                  class="archive-btn"
                  @click="archiveCooperateur(row)"
                  :disabled="archiving === row.id"
                >
                  {{ archiving === row.id ? 'En cours...' : 'Archiver' }}
                </button>
                <button
                  v-if="row.status === 'PAID'"
                  class="action-btn"
                  @click="markAsProcessed(row)"
                  :disabled="processing === row.id"
                >
                  {{ processing === row.id ? 'En cours...' : 'Marquer traitee' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Pagination controls -->
        <div class="pagination" v-if="totalPages > 1">
          <button class="pagination-btn" @click="goToPage(0)" :disabled="currentPage === 0">
            &laquo;
          </button>
          <button class="pagination-btn" @click="goToPage(currentPage - 1)" :disabled="currentPage === 0">
            &lsaquo;
          </button>
          <span class="pagination-info">
            Page {{ currentPage + 1 }} sur {{ totalPages }}
          </span>
          <button class="pagination-btn" @click="goToPage(currentPage + 1)" :disabled="currentPage >= totalPages - 1">
            &rsaquo;
          </button>
          <button class="pagination-btn" @click="goToPage(totalPages - 1)" :disabled="currentPage >= totalPages - 1">
            &raquo;
          </button>
          <select :value="pageSize" @change="changePageSize(Number(($event.target as HTMLSelectElement).value))" class="page-size-select">
            <option :value="10">10 / page</option>
            <option :value="20">20 / page</option>
            <option :value="50">50 / page</option>
            <option :value="100">100 / page</option>
          </select>
        </div>
      </div>
    </section>

    <section class="section" v-show="activeTab === 'supplementaires'">
      <div class="toolbar">
        <input
          v-model="querySupp"
          type="search"
          placeholder="Rechercher (nom, prénom, email)"
          class="search"
        />
        <div class="status-filter">
          <span class="filter-label">Statut:</span>
          <label v-for="status in allStatuses" :key="status" class="status-checkbox">
            <input
              type="checkbox"
              :value="status"
              v-model="selectedStatusesSupp"
              @change="loadSouscriptionsSupplementaires(0, pageSizeSupp)"
            />
            <span class="status" :class="statusClass(status)">{{ format(status) }}</span>
          </label>
        </div>
        <span class="meta" v-if="!loadingSupp && !errorSupp">{{ totalElementsSupp }} résultat(s)</span>
      </div>

      <div v-if="loadingSupp" class="state">Chargement…</div>
      <div v-else-if="errorSupp" class="state error">{{ errorSupp }}</div>

      <div v-else class="table-wrapper">
        <table class="table">
          <thead>
            <tr>
              <th v-for="col in columnsSupp" :key="col.key" @click="toggleSortSupp(col.key)" :class="['th', sortableClassSupp(col.key)]">
                <span>{{ col.label }}</span>
                <span class="sort-icon" aria-hidden="true" v-if="sortKeySupp === col.key">
                  {{ sortDirSupp === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in sortedRowsSupp" :key="row.id">
              <td>{{ row.id }}</td>
              <td>{{ row.prenom }}</td>
              <td>{{ row.nom }}</td>
              <td>{{ row.email }}</td>
              <td>{{ row.partsSupplementaires }}</td>
              <td>{{ (row.partsSupplementaires || 0) * 10 }} €</td>
              <td>
                <span class="status" :class="statusClass(row.status)">{{ format(row.status) }}</span>
              </td>
              <td>{{ formatDate(row.createdAt) }}</td>
              <td>{{ formatDate(row.updatedAt) }}</td>
              <td>
                <button
                  v-if="row.status === 'PAYMENT_PENDING'"
                  class="copy-btn"
                  @click="copyRetryLinkSupp(row)"
                >
                  {{ copiedIdSupp === row.id ? 'Copie !' : 'Copier lien' }}
                </button>
                <button
                  v-if="row.status === 'PAYMENT_PENDING'"
                  class="archive-btn"
                  @click="archiveSouscriptionSupplementaire(row)"
                  :disabled="archivingSupp === row.id"
                >
                  {{ archivingSupp === row.id ? 'En cours...' : 'Archiver' }}
                </button>
                <button
                  v-if="row.status === 'PAID'"
                  class="action-btn"
                  @click="markSuppAsProcessed(row)"
                  :disabled="processingSupp === row.id"
                >
                  {{ processingSupp === row.id ? 'En cours...' : 'Marquer traitee' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- Pagination controls -->
        <div class="pagination" v-if="totalPagesSupp > 1">
          <button class="pagination-btn" @click="goToPageSupp(0)" :disabled="currentPageSupp === 0">
            &laquo;
          </button>
          <button class="pagination-btn" @click="goToPageSupp(currentPageSupp - 1)" :disabled="currentPageSupp === 0">
            &lsaquo;
          </button>
          <span class="pagination-info">
            Page {{ currentPageSupp + 1 }} sur {{ totalPagesSupp }}
          </span>
          <button class="pagination-btn" @click="goToPageSupp(currentPageSupp + 1)" :disabled="currentPageSupp >= totalPagesSupp - 1">
            &rsaquo;
          </button>
          <button class="pagination-btn" @click="goToPageSupp(totalPagesSupp - 1)" :disabled="currentPageSupp >= totalPagesSupp - 1">
            &raquo;
          </button>
          <select :value="pageSizeSupp" @change="changePageSizeSupp(Number(($event.target as HTMLSelectElement).value))" class="page-size-select">
            <option :value="10">10 / page</option>
            <option :value="20">20 / page</option>
            <option :value="50">50 / page</option>
            <option :value="100">100 / page</option>
          </select>
        </div>
      </div>
    </section>

    <!-- Binome Modal -->
    <div v-if="selectedBinome" class="modal-overlay" @click.self="closeBinomeModal">
      <div class="modal">
        <div class="modal-header">
          <h2>Information du binôme</h2>
          <button class="modal-close" @click="closeBinomeModal">&times;</button>
        </div>
        <div class="modal-body">
          <dl class="binome-details">
            <div class="detail-row">
              <dt>Genre</dt>
              <dd>{{ format(selectedBinome.genre) }}</dd>
            </div>
            <div class="detail-row">
              <dt>Prénom</dt>
              <dd>{{ selectedBinome.prenom }}</dd>
            </div>
            <div class="detail-row">
              <dt>Nom</dt>
              <dd>{{ selectedBinome.nom }}</dd>
            </div>
            <div class="detail-row">
              <dt>Date de naissance</dt>
              <dd>{{ selectedBinome.dateNaissance || '—' }}</dd>
            </div>
            <div class="detail-row">
              <dt>Téléphone</dt>
              <dd>{{ selectedBinome.telephone || '—' }}</dd>
            </div>
            <div class="detail-row">
              <dt>Email</dt>
              <dd>{{ selectedBinome.email || '—' }}</dd>
            </div>
            <div class="detail-row">
              <dt>Adresse</dt>
              <dd>{{ selectedBinome.adresse || '—' }}</dd>
            </div>
            <div class="detail-row">
              <dt>Ville</dt>
              <dd>{{ selectedBinome.ville || '—' }}</dd>
            </div>
            <div class="detail-row">
              <dt>Code postal</dt>
              <dd>{{ selectedBinome.codePostal || '—' }}</dd>
            </div>
          </dl>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import type { CooperateurDTO, BinomeDTO, SouscriptionSupplementaireDTO } from '../api/model'
import { getSqqInscriptionAPI } from '../api/service/catalog'
import type { CooperateurStatus } from '../api/model'

type SortDir = 'asc' | 'desc'

const api = getSqqInscriptionAPI()
const allRows = ref<CooperateurDTO[]>([])
const rows = computed(() => {
  let filtered = allRows.value
  // Client-side search
  if (query.value) {
    const q = query.value.toLowerCase()
    filtered = filtered.filter(r =>
      r.nom?.toLowerCase().includes(q) ||
      r.prenom?.toLowerCase().includes(q) ||
      r.email?.toLowerCase().includes(q)
    )
  }
  // Update total elements based on filtered results
  return filtered
})
const loading = ref(true)
const error = ref<string | null>(null)
const query = ref('')
const selectedBinome = ref<BinomeDTO | null>(null)
const processing = ref<number | null>(null)
const archiving = ref<number | null>(null)

// Status filter state
const allStatuses: CooperateurStatus[] = ['PAYMENT_PENDING', 'PAID', 'PROCESSED', 'ARCHIVED']
const defaultStatuses: CooperateurStatus[] = ['PAYMENT_PENDING', 'PAID', 'PROCESSED']
const selectedStatuses = ref<CooperateurStatus[]>([...defaultStatuses])

// Pagination state for cooperateurs
const currentPage = ref(0)
const pageSize = ref(20)
const totalElements = computed(() => rows.value.length)
const totalPages = computed(() => Math.ceil(rows.value.length / pageSize.value) || 1)

const allRowsSupp = ref<SouscriptionSupplementaireDTO[]>([])
const rowsSupp = computed(() => {
  let filtered = allRowsSupp.value
  // Client-side search
  if (querySupp.value) {
    const q = querySupp.value.toLowerCase()
    filtered = filtered.filter(r =>
      r.nom?.toLowerCase().includes(q) ||
      r.prenom?.toLowerCase().includes(q) ||
      r.email?.toLowerCase().includes(q)
    )
  }
  return filtered
})
const loadingSupp = ref(true)
const errorSupp = ref<string | null>(null)
const querySupp = ref('')
const processingSupp = ref<number | null>(null)
const archivingSupp = ref<number | null>(null)
const copiedId = ref<number | null>(null)
const copiedIdSupp = ref<number | null>(null)
const selectedStatusesSupp = ref<CooperateurStatus[]>([...defaultStatuses])

// Pagination state for parts supplementaires
const currentPageSupp = ref(0)
const pageSizeSupp = ref(20)
const totalElementsSupp = computed(() => rowsSupp.value.length)
const totalPagesSupp = computed(() => Math.ceil(rowsSupp.value.length / pageSizeSupp.value) || 1)

// Tabs
const activeTab = ref<'inscriptions' | 'supplementaires'>('inscriptions')

// Load cooperateurs with status filter
async function loadCooperateurs(page = 0, size = 20) {
  try {
    loading.value = true
    error.value = null
    const resp = await api.getApiV1AdministrationCooperateurs({
      statuses: selectedStatuses.value.length > 0 ? selectedStatuses.value : undefined
    })
    const data = (resp as any).data ?? (resp as any)

    // Backend returns array, store all items
    const allItems = Array.isArray(data) ? data : (Array.isArray(data.content) ? data.content : [])
    allRows.value = allItems
    currentPage.value = Math.min(page, Math.max(0, Math.ceil(allItems.length / size) - 1))
    pageSize.value = size
  } catch (e: any) {
    error.value = e?.message ?? 'Une erreur est survenue lors du chargement.'
  } finally {
    loading.value = false
  }
}

// Load souscriptions supplementaires with status filter
async function loadSouscriptionsSupplementaires(page = 0, size = 20) {
  try {
    loadingSupp.value = true
    errorSupp.value = null
    const resp = await api.getApiV1AdministrationPartsAdditionnelles({
      statuses: selectedStatusesSupp.value.length > 0 ? selectedStatusesSupp.value : undefined
    })
    const data = (resp as any).data ?? (resp as any)

    // Backend returns array, store all items
    const allItems = Array.isArray(data) ? data : (Array.isArray(data.content) ? data.content : [])
    allRowsSupp.value = allItems
    currentPageSupp.value = Math.min(page, Math.max(0, Math.ceil(allItems.length / size) - 1))
    pageSizeSupp.value = size
  } catch (e: any) {
    errorSupp.value = e?.message ?? 'Une erreur est survenue lors du chargement.'
  } finally {
    loadingSupp.value = false
  }
}

// Pagination navigation for cooperateurs
function goToPage(page: number) {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
  }
}

function changePageSize(size: number) {
  pageSize.value = size
  currentPage.value = 0
}

// Pagination navigation for parts supplementaires
function goToPageSupp(page: number) {
  if (page >= 0 && page < totalPagesSupp.value) {
    currentPageSupp.value = page
  }
}

function changePageSizeSupp(size: number) {
  pageSizeSupp.value = size
  currentPageSupp.value = 0
}

// Watch search queries to reset pagination (search is client-side)
watch(query, () => {
  currentPage.value = 0
})

watch(querySupp, () => {
  currentPageSupp.value = 0
})

function openBinomeModal(binome: BinomeDTO) {
  selectedBinome.value = binome
}

function closeBinomeModal() {
  selectedBinome.value = null
}

async function markAsProcessed(row: CooperateurDTO) {
  if (!row.id) return
  processing.value = row.id
  try {
    const resp = await api.postApiV1AdministrationCooperateursIdProcess(row.id)
    const updated = (resp as any).data ?? resp
    const index = allRows.value.findIndex(r => r.id === row.id)
    if (index !== -1) {
      allRows.value[index] = updated
    }
  } catch (e: any) {
    alert('Erreur: ' + (e?.message ?? 'Une erreur est survenue'))
  } finally {
    processing.value = null
  }
}

async function markSuppAsProcessed(row: SouscriptionSupplementaireDTO) {
  if (!row.id) return
  processingSupp.value = row.id
  try {
    const resp = await api.postApiV1AdministrationPartsAdditionnellesIdProcess(row.id)
    const updated = (resp as any).data ?? resp
    const index = allRowsSupp.value.findIndex(r => r.id === row.id)
    if (index !== -1) {
      allRowsSupp.value[index] = updated
    }
  } catch (e: any) {
    alert('Erreur: ' + (e?.message ?? 'Une erreur est survenue'))
  } finally {
    processingSupp.value = null
  }
}

async function archiveCooperateur(row: CooperateurDTO) {
  if (!row.id || !confirm('Archiver cette souscription ?')) return
  archiving.value = row.id
  try {
    await api.postApiV1AdministrationCooperateursIdArchive(row.id)
    if (!selectedStatuses.value.includes('ARCHIVED')) {
      // Remove from allRows since ARCHIVED is not in filter
      allRows.value = allRows.value.filter(r => r.id !== row.id)
    } else {
      // Reload to get updated status
      await loadCooperateurs(currentPage.value, pageSize.value)
    }
  } catch (e: any) {
    alert('Erreur: ' + (e?.message ?? 'Une erreur est survenue'))
  } finally {
    archiving.value = null
  }
}

async function archiveSouscriptionSupplementaire(row: SouscriptionSupplementaireDTO) {
  if (!row.id || !confirm('Archiver cette souscription ?')) return
  archivingSupp.value = row.id
  try {
    await api.postApiV1AdministrationPartsAdditionnellesIdArchive(row.id)
    if (!selectedStatusesSupp.value.includes('ARCHIVED')) {
      // Remove from allRowsSupp since ARCHIVED is not in filter
      allRowsSupp.value = allRowsSupp.value.filter(r => r.id !== row.id)
    } else {
      // Reload to get updated status
      await loadSouscriptionsSupplementaires(currentPageSupp.value, pageSizeSupp.value)
    }
  } catch (e: any) {
    alert('Erreur: ' + (e?.message ?? 'Une erreur est survenue'))
  } finally {
    archivingSupp.value = null
  }
}

function copyRetryLink(row: CooperateurDTO) {
  if (!row.uuid) return
  const url = `${window.location.origin}/retry-payment?uuid=${row.uuid}`
  navigator.clipboard.writeText(url)
  copiedId.value = row.id ?? null
  setTimeout(() => { copiedId.value = null }, 2000)
}

function copyRetryLinkSupp(row: SouscriptionSupplementaireDTO) {
  if (!row.uuid) return
  const url = `${window.location.origin}/retry-payment?uuid=${row.uuid}&type=supplementaire`
  navigator.clipboard.writeText(url)
  copiedIdSupp.value = row.id ?? null
  setTimeout(() => { copiedIdSupp.value = null }, 2000)
}

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'genre', label: 'Genre' },
  { key: 'prenom', label: 'Prénom' },
  { key: 'nom', label: 'Nom' },
  { key: 'email', label: 'Email' },
  { key: 'telephone', label: 'Téléphone' },
  { key: 'adresse', label: 'Adresse' },
  { key: 'codePostal', label: 'Code postal' },
  { key: 'ville', label: 'Ville' },
  { key: 'etudiantOuMinimasSociaux', label: 'Tarif réduit' },
  { key: 'nombreDePersonnesDansLeFoyer', label: 'Pers. foyer' },
  { key: 'parts', label: 'Parts' },
  { key: 'partsDeSoutien', label: 'Parts de soutien' },
  { key: 'acceptationDesStatus', label: 'Statuts acceptés' },
  { key: 'binome', label: 'Binôme' },
  { key: 'status', label: 'Statut' },
  { key: 'createdAt', label: 'Créé le' },
  { key: 'updatedAt', label: 'Modifié le' },
  { key: 'actions', label: 'Actions' },
] as const

const columnsSupp = [
  { key: 'id', label: 'ID' },
  { key: 'prenom', label: 'Prénom' },
  { key: 'nom', label: 'Nom' },
  { key: 'email', label: 'Email' },
  { key: 'partsSupplementaires', label: 'Parts' },
  { key: 'montant', label: 'Montant' },
  { key: 'status', label: 'Statut' },
  { key: 'createdAt', label: 'Créé le' },
  { key: 'updatedAt', label: 'Modifié le' },
  { key: 'actions', label: 'Actions' },
] as const

type ColumnKey = typeof columns[number]['key']
type ColumnKeySupp = typeof columnsSupp[number]['key']

const sortKey = ref<ColumnKey>('createdAt')
const sortDir = ref<SortDir>('desc')

const sortKeySupp = ref<ColumnKeySupp>('createdAt')
const sortDirSupp = ref<SortDir>('desc')

function sortableClass(key: ColumnKey) {
  return sortKey.value === key ? (sortDir.value === 'asc' ? 'sorted-asc' : 'sorted-desc') : 'sortable'
}

function sortableClassSupp(key: ColumnKeySupp) {
  return sortKeySupp.value === key ? (sortDirSupp.value === 'asc' ? 'sorted-asc' : 'sorted-desc') : 'sortable'
}

function toggleSort(key: ColumnKey) {
  if (sortKey.value === key) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKey.value = key
    sortDir.value = 'asc'
  }
}

function toggleSortSupp(key: ColumnKeySupp) {
  if (sortKeySupp.value === key) {
    sortDirSupp.value = sortDirSupp.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKeySupp.value = key
    sortDirSupp.value = 'asc'
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

function formatDate(v?: string) {
  if (!v) return '—'
  const date = new Date(v)
  return date.toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function format(v: unknown) {
  switch (safeString(v)) {
    case 'PAYMENT_PENDING':
      return 'Paiement en attente'
    case 'PAID':
      return 'Payé'
    case 'PROCESSED':
      return 'Traitée'
    case 'ARCHIVED':
      return 'Archivée'
    case 'MADAME':
      return 'Madame'
    case 'MONSIEUR':
      return 'Monsieur'
    default:
      return safeString(v)
  }
}

function statusClass(v?: CooperateurStatus) {
  switch (v) {
    case 'PAYMENT_PENDING':
      return 'pending'
    case 'PAID':
      return 'paid'
    case 'PROCESSED':
      return 'processed'
    case 'ARCHIVED':
      return 'archived'
    default:
      return 'unknown'
  }
}

// Client-side sorting and pagination
const sortedRows = computed(() => {
  const key = sortKey.value
  const dir = sortDir.value
  const arr = [...rows.value]
  arr.sort((a: any, b: any) => {
    const va = a?.[key]
    const vb = b?.[key]
    const sa = safeString(va)
    const sb = safeString(vb)
    if (sa < sb) return dir === 'asc' ? -1 : 1
    if (sa > sb) return dir === 'asc' ? 1 : -1
    return 0
  })
  // Client-side pagination
  const start = currentPage.value * pageSize.value
  const end = start + pageSize.value
  return arr.slice(start, end)
})

const sortedRowsSupp = computed(() => {
  const key = sortKeySupp.value
  const dir = sortDirSupp.value
  const arr = [...rowsSupp.value]
  arr.sort((a: any, b: any) => {
    const va = a?.[key]
    const vb = b?.[key]
    const sa = safeString(va)
    const sb = safeString(vb)
    if (sa < sb) return dir === 'asc' ? -1 : 1
    if (sa > sb) return dir === 'asc' ? 1 : -1
    return 0
  })
  // Client-side pagination
  const start = currentPageSupp.value * pageSizeSupp.value
  const end = start + pageSizeSupp.value
  return arr.slice(start, end)
})

onMounted(async () => {
  await loadCooperateurs(0, pageSize.value)
  await loadSouscriptionsSupplementaires(0, pageSizeSupp.value)
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
.status.pending {
  background: #ffedd5; /* orange-100 */
  color: #9a3412;     /* orange-700 */
}
.status.paid {
  background: #fef9c3; /* yellow-100 */
  color: #854d0e;      /* yellow-800 */
}
.status.processed {
  background: #dcfce7; /* green-100 */
  color: #166534;      /* green-700 */
}
.status.unknown {
  background: #e5e7eb; /* gray-200 */
  color: #374151;      /* gray-700 */
}

/* Binome column */
.binome-btn {
  background: #dbeafe;
  color: #1d4ed8;
  border: none;
  padding: .25rem .6rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: .85rem;
  font-weight: 500;
}
.binome-btn:hover {
  background: #bfdbfe;
}
.no-binome {
  color: #9ca3af;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal {
  background: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 480px;
  max-height: 90vh;
  overflow: auto;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.25rem;
  border-bottom: 1px solid #e5e7eb;
}
.modal-header h2 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
}
.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  line-height: 1;
  color: #6b7280;
  cursor: pointer;
}
.modal-close:hover {
  color: #1f2937;
}
.modal-body {
  padding: 1.25rem;
}
.binome-details {
  margin: 0;
}
.detail-row {
  display: flex;
  padding: .5rem 0;
  border-bottom: 1px solid #f3f4f6;
}
.detail-row:last-child {
  border-bottom: none;
}
.detail-row dt {
  flex: 0 0 140px;
  font-weight: 500;
  color: #6b7280;
}
.detail-row dd {
  margin: 0;
  color: #1f2937;
}

/* Action button */
.action-btn {
  background: #10b981;
  color: white;
  border: none;
  padding: .35rem .75rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: .8rem;
  font-weight: 500;
  white-space: nowrap;
}
.action-btn:hover:not(:disabled) {
  background: #059669;
}
.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.processed-label {
  color: #166534;
  font-size: .85rem;
}

/* Copy link button */
.copy-btn {
  background: #f59e0b;
  color: white;
  border: none;
  padding: .35rem .75rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: .8rem;
  font-weight: 500;
  white-space: nowrap;
}
.copy-btn:hover {
  background: #d97706;
}

/* Tabs */
.tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.tab {
  padding: 0.75rem 1.25rem;
  border: none;
  background: #e5e7eb;
  color: #374151;
  font-size: 0.95rem;
  font-weight: 600;
  border-radius: 8px 8px 8px 8px;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.tab:hover {
  background: #d1d5db;
}

.tab.active {
  background: #fff;
  color: #1f2937;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}

.tab-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 1.5rem;
  height: 1.5rem;
  padding: 0 0.4rem;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 700;
}

.tab.active .tab-count {
  background: rgba(0, 0, 0, 0.08);
}

/* Status filter */
.status-filter {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.filter-label {
  font-size: 0.85rem;
  color: #6b7280;
  white-space: nowrap;
}

.status-checkbox {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  cursor: pointer;
}

.status-checkbox input {
  cursor: pointer;
}

/* Archived status badge */
.status.archived {
  background: #e5e7eb;
  color: #6b7280;
}

/* Archive button */
.archive-btn {
  background: #6b7280;
  color: white;
  border: none;
  padding: .35rem .75rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: .8rem;
  font-weight: 500;
  white-space: nowrap;
  margin-left: 0.25rem;
}

.archive-btn:hover:not(:disabled) {
  background: #4b5563;
}

.archive-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Pagination controls */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem 0;
  border-top: 1px solid #e5e7eb;
  margin-top: 1rem;
}

.pagination-btn {
  padding: 0.5rem 0.75rem;
  border: 1px solid #e5e7eb;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: #f9fafb;
  border-color: #d1d5db;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  padding: 0 1rem;
  color: #6b7280;
  font-size: 0.9rem;
}

.page-size-select {
  padding: 0.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  background: #fff;
  font-size: 0.85rem;
  cursor: pointer;
  margin-left: 1rem;
}
</style>
