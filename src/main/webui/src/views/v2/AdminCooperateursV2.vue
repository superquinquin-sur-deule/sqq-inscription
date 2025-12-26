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
        <span class="meta" v-if="!loading && !error">{{ totalElements }} résultat(s)</span>
        <button class="create-btn" @click="openCreateModal">+ Nouveau membre</button>
        <ColumnPicker
          :visible-columns="visibleColumnKeys"
          @update:visible-columns="visibleColumnKeys = $event"
        />
      </div>
      <FilterBar
        v-model="filters"
        @update:model-value="handleFilterChange"
      />

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
              class="clickable-row"
              @click="openMemberDetail(row)"
            >
              <td v-for="col in columns" :key="col.key">
                <!-- ID -->
                <template v-if="col.key === 'id'">{{ row.id }}</template>
                <!-- Nom -->
                <template v-else-if="col.key === 'nom'">{{ row.nom }}</template>
                <!-- Prénom -->
                <template v-else-if="col.key === 'prenom'">{{ row.prenom }}</template>
                <!-- Email -->
                <template v-else-if="col.key === 'email'">{{ row.email }}</template>
                <!-- Téléphone -->
                <template v-else-if="col.key === 'telephone'">{{ row.telephone }}</template>
                <!-- Type -->
                <template v-else-if="col.key === 'memberType'">
                  <span class="type-badge" :class="typeClass(row.memberType)">
                    {{ row.memberType === 'BINOME' ? 'Binôme' : 'Principal' }}
                  </span>
                </template>
                <!-- Partner -->
                <template v-else-if="col.key === 'partnerName'">
                  <button
                    v-if="row.partnerId"
                    class="partner-link"
                    @click.stop="scrollToPartner(row.partnerId)"
                  >
                    {{ row.partnerName }}
                  </button>
                  <span v-else class="no-partner">—</span>
                </template>
                <!-- Member Status -->
                <template v-else-if="col.key === 'memberStatus'">
                  <span class="status-badge" :class="memberStatusClass(row.memberStatus)">
                    {{ formatMemberStatus(row.memberStatus) }}
                  </span>
                </template>
                <!-- Payment Status -->
                <template v-else-if="col.key === 'paymentStatus'">
                  <span class="status-badge" :class="paymentStatusClass(row.paymentStatus)">
                    {{ formatPaymentStatus(row.paymentStatus) }}
                  </span>
                </template>
                <!-- Total Parts -->
                <template v-else-if="col.key === 'totalParts'">{{ row.totalParts ?? 0 }}</template>
                <!-- Created At -->
                <template v-else-if="col.key === 'createdAt'">{{ formatDate(row.createdAt) }}</template>
                <!-- Adresse -->
                <template v-else-if="col.key === 'adresse'">{{ row.adresse || '—' }}</template>
                <!-- Ville -->
                <template v-else-if="col.key === 'ville'">{{ row.ville || '—' }}</template>
                <!-- Code Postal -->
                <template v-else-if="col.key === 'codePostal'">{{ row.codePostal || '—' }}</template>
                <!-- Date Naissance -->
                <template v-else-if="col.key === 'dateNaissance'">{{ formatDate(row.dateNaissance) }}</template>
                <!-- Tarif réduit -->
                <template v-else-if="col.key === 'etudiantOuMinimasSociaux'">{{ formatBoolean(row.etudiantOuMinimasSociaux) }}</template>
                <!-- Personnes foyer -->
                <template v-else-if="col.key === 'nombreDePersonnesDansLeFoyer'">{{ row.nombreDePersonnesDansLeFoyer ?? '—' }}</template>
                <!-- Source -->
                <template v-else-if="col.key === 'source'">{{ formatSource(row.source) }}</template>
                <!-- Notes -->
                <template v-else-if="col.key === 'notes'">
                  <span class="notes-cell">{{ row.notes || '—' }}</span>
                </template>
                <!-- Updated At -->
                <template v-else-if="col.key === 'updatedAt'">{{ formatDate(row.updatedAt) }}</template>
              </td>
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

    <!-- Member Detail Modal -->
    <MemberDetailModal
      :member="selectedMember"
      :is-open="isModalOpen"
      @close="closeMemberDetail"
      @navigate-to-partner="handleNavigateToPartner"
    />

    <!-- Create Member Modal -->
    <CreateMemberModal
      :is-open="isCreateModalOpen"
      @close="closeCreateModal"
      @created="handleMemberCreated"
    />
  </main>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import axios from 'axios'
import ColumnPicker from '../../components/v2/ColumnPicker.vue'
import FilterBar, { type FilterState } from '../../components/v2/FilterBar.vue'
import MemberDetailModal from '../../components/v2/MemberDetailModal.vue'
import CreateMemberModal from '../../components/v2/CreateMemberModal.vue'

interface ColumnConfig {
  key: string
  label: string
  default: boolean
  sortable: boolean
}

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
const highlightedId = ref<number | null>(null)

// Member detail modal state
const selectedMember = ref<CooperateurV2DTO | null>(null)
const isModalOpen = ref(false)

// Create modal state
const isCreateModalOpen = ref(false)

// Filter state
const filters = ref<FilterState>({
  search: '',
  type: null,
  memberStatus: null,
  paymentStatus: null,
  source: null,
  createdFrom: null,
  createdTo: null,
})

// Pagination state
const page = ref(0)
const size = ref(20)
const totalElements = ref(0)
const totalPages = ref(0)

// Sort state
const sortKey = ref('createdAt')
const sortDir = ref<SortDir>('desc')

// All available columns with their configuration
const allColumns: ColumnConfig[] = [
  // Default visible
  { key: 'id', label: 'ID', default: true, sortable: true },
  { key: 'nom', label: 'Nom', default: true, sortable: true },
  { key: 'prenom', label: 'Prénom', default: true, sortable: true },
  { key: 'email', label: 'Email', default: true, sortable: true },
  { key: 'telephone', label: 'Téléphone', default: true, sortable: false },
  { key: 'memberType', label: 'Type', default: true, sortable: true },
  { key: 'partnerName', label: 'Lié à', default: true, sortable: false },
  { key: 'memberStatus', label: 'Statut membre', default: true, sortable: true },
  { key: 'paymentStatus', label: 'Statut paiement', default: true, sortable: true },
  { key: 'totalParts', label: 'Parts', default: true, sortable: true },
  { key: 'createdAt', label: 'Créé le', default: true, sortable: true },
  // Hidden by default
  { key: 'adresse', label: 'Adresse', default: false, sortable: false },
  { key: 'ville', label: 'Ville', default: false, sortable: true },
  { key: 'codePostal', label: 'Code postal', default: false, sortable: true },
  { key: 'dateNaissance', label: 'Date naissance', default: false, sortable: true },
  { key: 'etudiantOuMinimasSociaux', label: 'Tarif réduit', default: false, sortable: true },
  { key: 'nombreDePersonnesDansLeFoyer', label: 'Pers. foyer', default: false, sortable: true },
  { key: 'source', label: 'Source', default: false, sortable: true },
  { key: 'notes', label: 'Notes', default: false, sortable: false },
  { key: 'updatedAt', label: 'Modifié le', default: false, sortable: true },
]

const STORAGE_KEY = 'v2-admin-grid-columns'

// Load visible columns from localStorage or use defaults
function loadVisibleColumns(): string[] {
  try {
    const saved = localStorage.getItem(STORAGE_KEY)
    if (saved) {
      const parsed = JSON.parse(saved) as string[]
      // Validate that keys still exist
      const validKeys = allColumns.map(c => c.key)
      const validSaved = parsed.filter(k => validKeys.includes(k))
      if (validSaved.length > 0) {
        return validSaved
      }
    }
  } catch {
    // localStorage not available or invalid data
  }
  return allColumns.filter(c => c.default).map(c => c.key)
}

const visibleColumnKeys = ref<string[]>(loadVisibleColumns())

// Computed property for visible columns with their full config
const columns = computed(() => {
  return allColumns.filter(col => visibleColumnKeys.value.includes(col.key))
})

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

    // Apply filters
    if (filters.value.search.trim()) {
      params.set('search', filters.value.search.trim())
    }
    if (filters.value.type) {
      params.set('type', filters.value.type)
    }
    if (filters.value.memberStatus) {
      params.set('memberStatus', filters.value.memberStatus)
    }
    if (filters.value.paymentStatus) {
      params.set('paymentStatus', filters.value.paymentStatus)
    }
    if (filters.value.source) {
      params.set('source', filters.value.source)
    }
    if (filters.value.createdFrom) {
      params.set('createdFrom', filters.value.createdFrom)
    }
    if (filters.value.createdTo) {
      params.set('createdTo', filters.value.createdTo)
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

function handleFilterChange() {
  page.value = 0 // Reset to first page when filters change
  loadData()
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

function formatSource(source?: string) {
  switch (source) {
    case 'ONLINE_REGISTRATION': return 'Inscription en ligne'
    case 'CSV_IMPORT': return 'Import CSV'
    case 'MANUAL_ENTRY': return 'Saisie manuelle'
    default: return source ?? '—'
  }
}

function formatBoolean(value?: boolean) {
  if (value === undefined || value === null) return '—'
  return value ? 'Oui' : 'Non'
}

function openMemberDetail(member: CooperateurV2DTO) {
  selectedMember.value = member
  isModalOpen.value = true
}

function closeMemberDetail() {
  isModalOpen.value = false
  selectedMember.value = null
}

function handleNavigateToPartner(partnerId: number) {
  closeMemberDetail()
  scrollToPartner(partnerId)
}

function openCreateModal() {
  isCreateModalOpen.value = true
}

function closeCreateModal() {
  isCreateModalOpen.value = false
}

function handleMemberCreated() {
  loadData() // Refresh the list
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
.toolbar { display: flex; align-items: center; justify-content: flex-end; gap: .75rem; margin-bottom: .5rem; }
.meta { color: #6b7280; font-size: .9rem; margin-right: auto; }
.create-btn {
  padding: .4rem .85rem;
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: .9rem;
  font-weight: 500;
  transition: background 0.15s;
}
.create-btn:hover {
  background: #1d4ed8;
}
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

tr.clickable-row {
  cursor: pointer;
  transition: background-color 0.1s;
}

tr.clickable-row:hover {
  background: #f9fafb;
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

.notes-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: block;
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
