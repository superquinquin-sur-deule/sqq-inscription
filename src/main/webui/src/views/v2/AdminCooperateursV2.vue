<template>
  <main class="container">
    <header class="header">
      <div class="brand">
        <img src="/superquinquin_logo_deule.svg" alt="SuperQuinquin sur Deûle" class="logo" />
        <div class="brand-text">
          <h1>Coopérateurs V2</h1>
          <p class="subtitle">Administration — liste des coopérateurs (modèle V2)</p>
        </div>
      </div>
    </header>

    <section class="section">
      <div class="toolbar">
        <input
          v-model="searchQuery"
          type="search"
          placeholder="Rechercher (tous champs)"
          class="search"
          @keyup.enter="loadData"
        />
        <button class="search-btn" @click="loadData">Rechercher</button>
        <span class="meta" v-if="!loading && !error">{{ totalElements }} résultat(s)</span>
      </div>

      <div v-if="loading" class="state">Chargement…</div>
      <div v-else-if="error" class="state error">
        {{ error }}
        <button class="retry-btn" @click="loadData">Réessayer</button>
      </div>

      <div v-else class="table-wrapper">
        <table class="table">
          <thead>
            <tr>
              <th
                v-for="col in columns"
                :key="col.key"
                @click="col.sortable ? toggleSort(col.key) : null"
                :class="['th', col.sortable ? sortableClass(col.key) : '']"
              >
                <span>{{ col.label }}</span>
                <span class="sort-icon" aria-hidden="true" v-if="sortKey === col.key">
                  {{ sortDir === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="row in rows"
              :key="row.id"
              :id="`row-${row.id}`"
              :class="{ 'highlighted': highlightedId === row.id }"
            >
              <td>{{ row.id }}</td>
              <td>{{ row.nom }}</td>
              <td>{{ row.prenom }}</td>
              <td>{{ row.email }}</td>
              <td>{{ row.telephone }}</td>
              <td>
                <span class="type-badge" :class="typeClass(row.memberType)">
                  {{ row.memberType === 'BINOME' ? 'Binôme' : 'Principal' }}
                </span>
              </td>
              <td>
                <button
                  v-if="row.partnerId"
                  class="partner-link"
                  @click="scrollToPartner(row.partnerId)"
                >
                  {{ row.partnerName }}
                </button>
                <span v-else class="no-partner">—</span>
              </td>
              <td>
                <span class="status-badge" :class="memberStatusClass(row.memberStatus)">
                  {{ formatMemberStatus(row.memberStatus) }}
                </span>
              </td>
              <td>
                <span class="status-badge" :class="paymentStatusClass(row.paymentStatus)">
                  {{ formatPaymentStatus(row.paymentStatus) }}
                </span>
              </td>
              <td>{{ row.totalParts ?? 0 }}</td>
              <td>{{ formatDate(row.createdAt) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="!loading && !error && totalPages > 1" class="pagination">
        <button
          class="page-btn"
          :disabled="page === 0"
          @click="goToPage(0)"
        >
          ««
        </button>
        <button
          class="page-btn"
          :disabled="page === 0"
          @click="goToPage(page - 1)"
        >
          «
        </button>
        <span class="page-info">Page {{ page + 1 }} sur {{ totalPages }}</span>
        <button
          class="page-btn"
          :disabled="page >= totalPages - 1"
          @click="goToPage(page + 1)"
        >
          »
        </button>
        <button
          class="page-btn"
          :disabled="page >= totalPages - 1"
          @click="goToPage(totalPages - 1)"
        >
          »»
        </button>
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import axios from 'axios'

interface CooperateurV2DTO {
  id: number
  genre: string
  prenom: string
  nom: string
  dateNaissance?: string
  telephone: string
  email: string
  adresse: string
  ville: string
  codePostal: string
  etudiantOuMinimasSociaux?: boolean
  nombreDePersonnesDansLeFoyer?: number
  paymentStatus: string
  memberStatus: string
  source?: string
  notes?: string
  createdAt: string
  updatedAt: string
  totalParts: number
  memberType: string
  partnerId?: number
  partnerName?: string
}

interface PagedResponse {
  content: CooperateurV2DTO[]
  totalElements: number
  totalPages: number
  page: number
  size: number
  first: boolean
  last: boolean
}

type SortDir = 'asc' | 'desc'

const rows = ref<CooperateurV2DTO[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const searchQuery = ref('')
const highlightedId = ref<number | null>(null)

// Pagination state
const page = ref(0)
const size = ref(20)
const totalElements = ref(0)
const totalPages = ref(0)

// Sort state
const sortKey = ref('createdAt')
const sortDir = ref<SortDir>('desc')

const columns = [
  { key: 'id', label: 'ID', sortable: true },
  { key: 'nom', label: 'Nom', sortable: true },
  { key: 'prenom', label: 'Prénom', sortable: true },
  { key: 'email', label: 'Email', sortable: true },
  { key: 'telephone', label: 'Téléphone', sortable: false },
  { key: 'memberType', label: 'Type', sortable: false },
  { key: 'partnerName', label: 'Lié à', sortable: false },
  { key: 'memberStatus', label: 'Statut membre', sortable: true },
  { key: 'paymentStatus', label: 'Statut paiement', sortable: true },
  { key: 'totalParts', label: 'Parts', sortable: false },
  { key: 'createdAt', label: 'Créé le', sortable: true },
]

function sortableClass(key: string) {
  return sortKey.value === key ? (sortDir.value === 'asc' ? 'sorted-asc' : 'sorted-desc') : 'sortable'
}

function toggleSort(key: string) {
  if (sortKey.value === key) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKey.value = key
    sortDir.value = 'asc'
  }
  loadData()
}

function goToPage(newPage: number) {
  page.value = newPage
  loadData()
}

async function loadData() {
  loading.value = true
  error.value = null

  try {
    const params = new URLSearchParams()
    params.set('page', String(page.value))
    params.set('size', String(size.value))
    params.set('sort', `${sortKey.value},${sortDir.value}`)

    if (searchQuery.value.trim()) {
      params.set('search', searchQuery.value.trim())
    }

    const response = await axios.get<PagedResponse>(
      `/api/v2/administration/cooperateurs?${params.toString()}`
    )

    const data = response.data
    rows.value = data.content
    totalElements.value = data.totalElements
    totalPages.value = data.totalPages
    page.value = data.page
  } catch (e: any) {
    error.value = e?.response?.data ?? e?.message ?? 'Une erreur est survenue lors du chargement.'
  } finally {
    loading.value = false
  }
}

function scrollToPartner(partnerId: number) {
  // Check if partner is on current page
  const partnerRow = rows.value.find(r => r.id === partnerId)
  if (partnerRow) {
    const element = document.getElementById(`row-${partnerId}`)
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'center' })
      highlightedId.value = partnerId
      setTimeout(() => {
        highlightedId.value = null
      }, 2000)
    }
  } else {
    // TODO: Navigate to the page containing the partner
    alert(`Le partenaire (ID: ${partnerId}) n'est pas sur cette page.`)
  }
}

function formatDate(v?: string) {
  if (!v) return '—'
  const date = new Date(v)
  return date.toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

function formatMemberStatus(status?: string) {
  switch (status) {
    case 'PROSPECT': return 'Prospect'
    case 'ACTIVE': return 'Actif'
    case 'SUSPENDED': return 'Suspendu'
    case 'DEPARTED': return 'Parti'
    default: return status ?? '—'
  }
}

function formatPaymentStatus(status?: string) {
  switch (status) {
    case 'PENDING': return 'En attente'
    case 'PAID': return 'Payé'
    default: return status ?? '—'
  }
}

function memberStatusClass(status?: string) {
  switch (status) {
    case 'PROSPECT': return 'status-prospect'
    case 'ACTIVE': return 'status-active'
    case 'SUSPENDED': return 'status-suspended'
    case 'DEPARTED': return 'status-departed'
    default: return 'status-unknown'
  }
}

function paymentStatusClass(status?: string) {
  switch (status) {
    case 'PENDING': return 'status-pending'
    case 'PAID': return 'status-paid'
    default: return 'status-unknown'
  }
}

function typeClass(type?: string) {
  return type === 'BINOME' ? 'type-binome' : 'type-principal'
}

onMounted(() => {
  loadData()
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
.search-btn {
  padding: .5rem 1rem;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
}
.search-btn:hover {
  background: #2563eb;
}
.meta { color: #6b7280; font-size: .9rem; }
.state { padding: 1rem; color: #374151; }
.state.error { color: #b91c1c; display: flex; align-items: center; gap: 1rem; }
.retry-btn {
  padding: .35rem .75rem;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: .85rem;
}
.retry-btn:hover {
  background: #dc2626;
}

.table-wrapper { overflow: auto; }
.table { width: 100%; border-collapse: collapse; }
.table thead th { position: sticky; top: 0; background: #fafafa; }
.th { text-align: left; padding: .5rem .5rem; border-bottom: 1px solid #e5e7eb; cursor: pointer; user-select: none; white-space: nowrap; }
td { padding: .5rem .5rem; border-bottom: 1px solid #f3f4f6; }
.th.sortable:hover { background: #f9fafb; }
.sorted-asc, .sorted-desc { background: #f9fafb; }
.sort-icon { margin-left: .35rem; font-size: .75rem; color: #6b7280; }

/* Row highlight */
tr.highlighted {
  animation: highlight 2s ease-out;
}
@keyframes highlight {
  0% { background: #fef08a; }
  100% { background: transparent; }
}

/* Type badges */
.type-badge {
  display: inline-block;
  padding: .15rem .5rem;
  border-radius: 999px;
  font-size: .85rem;
  font-weight: 500;
}
.type-principal {
  background: #e5e7eb;
  color: #374151;
}
.type-binome {
  background: #dbeafe;
  color: #1d4ed8;
}

/* Status badges */
.status-badge {
  display: inline-block;
  padding: .15rem .5rem;
  border-radius: 999px;
  font-size: .85rem;
  font-weight: 600;
  line-height: 1.2;
}
.status-prospect {
  background: #e0e7ff;
  color: #3730a3;
}
.status-active {
  background: #dcfce7;
  color: #166534;
}
.status-suspended {
  background: #ffedd5;
  color: #9a3412;
}
.status-departed {
  background: #e5e7eb;
  color: #374151;
}
.status-pending {
  background: #fef9c3;
  color: #854d0e;
}
.status-paid {
  background: #dcfce7;
  color: #166534;
}
.status-unknown {
  background: #e5e7eb;
  color: #374151;
}

/* Partner link */
.partner-link {
  background: none;
  border: none;
  color: #2563eb;
  cursor: pointer;
  font-size: inherit;
  text-decoration: underline;
  padding: 0;
}
.partner-link:hover {
  color: #1d4ed8;
}
.no-partner {
  color: #9ca3af;
}

/* Pagination */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: .5rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e5e7eb;
}
.page-btn {
  padding: .35rem .65rem;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
}
.page-btn:hover:not(:disabled) {
  background: #e5e7eb;
}
.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
.page-info {
  padding: 0 1rem;
  color: #6b7280;
  font-size: .9rem;
}
</style>
