<template>
  <main class="bg-bg-alt w-full max-w-none m-0 py-8 px-4 text-text rounded-none">
    <header class="py-4 px-5 mb-4">
      <div class="flex items-center gap-4">
        <img src="/superquinquin_logo_deule.svg" alt="SuperQuinquin sur Deûle" class="h-[72px]" />
        <div class="flex flex-col">
          <h1 class="m-0 mb-[0.35rem] text-3xl font-semibold">Coopérateurs</h1>
          <p class="text-sm text-muted m-0">Administration — liste des coopérateurs enregistrés</p>
        </div>
      </div>
    </header>

    <div class="flex gap-2 mb-4">
      <button
        class="py-3 px-5 border-none bg-tab-bg text-tab-text text-[0.95rem] font-semibold rounded-section cursor-pointer transition-all duration-200 flex items-center gap-2 hover:bg-tab-hover"
        :class="{ 'bg-white text-text shadow-tab-active': activeTab === 'inscriptions' }"
        @click="activeTab = 'inscriptions'"
      >
        Souscriptions
        <span class="inline-flex items-center justify-center min-w-[1.5rem] h-6 px-[0.4rem] bg-black/10 rounded-full text-sm font-bold" :class="{ 'bg-black/[0.08]': activeTab === 'inscriptions' }">{{ totalElements }}</span>
      </button>
      <button
        class="py-3 px-5 border-none bg-tab-bg text-tab-text text-[0.95rem] font-semibold rounded-section cursor-pointer transition-all duration-200 flex items-center gap-2 hover:bg-tab-hover"
        :class="{ 'bg-white text-text shadow-tab-active': activeTab === 'supplementaires' }"
        @click="activeTab = 'supplementaires'"
      >
        Parts supplémentaires
        <span class="inline-flex items-center justify-center min-w-[1.5rem] h-6 px-[0.4rem] bg-black/10 rounded-full text-sm font-bold" :class="{ 'bg-black/[0.08]': activeTab === 'supplementaires' }">{{ totalElementsSupp }}</span>
      </button>
    </div>

    <section class="bg-white border border-border rounded-section p-4" v-show="activeTab === 'inscriptions'">
      <div class="flex items-center gap-3 mb-3 flex-wrap">
        <input
          v-model="query"
          type="search"
          placeholder="Rechercher (nom, prénom, email)"
          class="flex-1 py-2 px-3 border border-border-gray rounded-section"
        />
        <div class="flex items-center gap-2 flex-wrap">
          <span class="text-sm text-muted whitespace-nowrap">Statut:</span>
          <label v-for="status in allStatuses" :key="status" class="flex items-center gap-1 cursor-pointer">
            <input
              type="checkbox"
              :value="status"
              v-model="selectedStatuses"
              @change="loadCooperateurs(0, pageSize)"
              class="cursor-pointer"
            />
            <span
              class="inline-block py-1.5 px-2 rounded-sm text-sm font-semibold leading-[1.2]"
              :class="{
                'bg-status-pending-bg text-status-pending-text': status === 'PAYMENT_PENDING',
                'bg-status-paid-bg text-status-paid-text': status === 'PAID',
                'bg-status-processed-bg text-status-processed-text': status === 'PROCESSED',
                'bg-status-archived-bg text-status-archived-text': status === 'ARCHIVED'
              }"
            >{{ format(status) }}</span>
          </label>
        </div>
        <span class="text-muted text-sm" v-if="!loading && !error">{{ totalElements }} résultat(s)</span>
      </div>

      <div v-if="loading" class="p-4 text-tab-text">Chargement…</div>
      <div v-else-if="error" class="p-4 text-red-700">{{ error }}</div>

      <div v-else class="overflow-auto">
        <table class="w-full border-collapse">
          <thead>
            <tr>
              <th v-for="col in columns" :key="col.key" @click="toggleSort(col.key)" class="sticky top-0 bg-surface-gray text-left py-2 px-2 border-b border-border-gray cursor-pointer select-none whitespace-nowrap hover:bg-gray-100">
                <span>{{ col.label }}</span>
                <span class="ml-[0.35rem] text-xs text-muted" aria-hidden="true" v-if="sortKey === col.key">
                  {{ sortDir === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in sortedRows" :key="row.id">
              <td class="py-2 px-2 border-b border-border-light">{{ row.id }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ format(row.genre) }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.prenom }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.nom }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.email }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.telephone }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.adresse }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.codePostal }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.ville }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ yesNo(row.etudiantOuMinimasSociaux) }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.nombreDePersonnesDansLeFoyer }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.parts }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.partsDeSoutien }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ yesNo(row.acceptationDesStatus) }}</td>
              <td class="py-2 px-2 border-b border-border-light">
                <button
                    v-if="row.binome"
                    class="bg-binome-bg text-binome-text border-none py-1 px-[0.6rem] rounded cursor-pointer text-sm font-medium hover:bg-binome-hover"
                    @click="openBinomeModal(row.binome)"
                >
                  Oui
                </button>
                <span v-else class="text-gray-400">Non</span>
              </td>
              <td class="py-2 px-2 border-b border-border-light">
                <span
                  class="inline-block py-1 px-2 rounded-sm text-sm font-semibold leading-[1.2]"
                  :class="{
                    'bg-status-pending-bg text-status-pending-text': row.status === 'PAYMENT_PENDING',
                    'bg-status-paid-bg text-status-paid-text': row.status === 'PAID',
                    'bg-status-processed-bg text-status-processed-text': row.status === 'PROCESSED',
                    'bg-status-archived-bg text-status-archived-text': row.status === 'ARCHIVED'
                  }"
                >{{ format(row.status) }}</span>
              </td>
              <td class="py-2 px-2 border-b border-border-light">{{ formatDate(row.createdAt) }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ formatDate(row.updatedAt) }}</td>
              <td class="py-2 px-2 border-b border-border-light">
                <button
                  v-if="row.status === 'PAYMENT_PENDING'"
                  class="bg-action-orange text-white border-none py-[0.35rem] px-3 rounded cursor-pointer text-sm font-medium whitespace-nowrap hover:bg-action-orange-hover"
                  @click="copyRetryLink(row)"
                >
                  {{ copiedId === row.id ? 'Copie !' : 'Copier lien' }}
                </button>
                <button
                  v-if="row.status === 'PAYMENT_PENDING'"
                  class="bg-action-gray text-white border-none py-[0.35rem] px-3 rounded cursor-pointer text-sm font-medium whitespace-nowrap ml-1 hover:bg-action-gray-hover disabled:opacity-60 disabled:cursor-not-allowed"
                  @click="archiveCooperateur(row)"
                  :disabled="archiving === row.id"
                >
                  {{ archiving === row.id ? 'En cours...' : 'Archiver' }}
                </button>
                <button
                  v-if="row.status === 'PAID'"
                  class="bg-action-green text-white border-none py-[0.35rem] px-3 rounded cursor-pointer text-sm font-medium whitespace-nowrap hover:bg-action-green-hover disabled:opacity-60 disabled:cursor-not-allowed"
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
        <div class="flex items-center justify-center gap-2 py-4 border-t border-border-gray mt-4" v-if="totalPages > 1">
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="goToPage(0)" :disabled="currentPage === 0">
            &laquo;
          </button>
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="goToPage(currentPage - 1)" :disabled="currentPage === 0">
            &lsaquo;
          </button>
          <span class="px-4 text-muted text-sm">
            Page {{ currentPage + 1 }} sur {{ totalPages }}
          </span>
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="goToPage(currentPage + 1)" :disabled="currentPage >= totalPages - 1">
            &rsaquo;
          </button>
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="goToPage(totalPages - 1)" :disabled="currentPage >= totalPages - 1">
            &raquo;
          </button>
          <select :value="pageSize" @change="changePageSize(Number(($event.target as HTMLSelectElement).value))" class="py-2 px-2 border border-border-gray rounded bg-white text-sm cursor-pointer ml-4">
            <option :value="10">10 / page</option>
            <option :value="20">20 / page</option>
            <option :value="50">50 / page</option>
            <option :value="100">100 / page</option>
          </select>
        </div>
      </div>
    </section>

    <section class="bg-white border border-border rounded-section p-4" v-show="activeTab === 'supplementaires'">
      <div class="flex items-center gap-3 mb-3 flex-wrap">
        <input
          v-model="querySupp"
          type="search"
          placeholder="Rechercher (nom, prénom, email)"
          class="flex-1 py-2 px-3 border border-border-gray rounded-section"
        />
        <div class="flex items-center gap-2 flex-wrap">
          <span class="text-sm text-muted whitespace-nowrap">Statut:</span>
          <label v-for="status in allStatuses" :key="status" class="flex items-center gap-1 cursor-pointer">
            <input
              type="checkbox"
              :value="status"
              v-model="selectedStatusesSupp"
              @change="loadSouscriptionsSupplementaires(0, pageSizeSupp)"
              class="cursor-pointer"
            />
            <span
              class="inline-block py-1 px-2 rounded-sm text-sm font-semibold leading-[1.2]"
              :class="{
                'bg-status-pending-bg text-status-pending-text': status === 'PAYMENT_PENDING',
                'bg-status-paid-bg text-status-paid-text': status === 'PAID',
                'bg-status-processed-bg text-status-processed-text': status === 'PROCESSED',
                'bg-status-archived-bg text-status-archived-text': status === 'ARCHIVED'
              }"
            >{{ format(status) }}</span>
          </label>
        </div>
        <span class="text-muted text-sm" v-if="!loadingSupp && !errorSupp">{{ totalElementsSupp }} résultat(s)</span>
      </div>

      <div v-if="loadingSupp" class="p-4 text-tab-text">Chargement…</div>
      <div v-else-if="errorSupp" class="p-4 text-red-700">{{ errorSupp }}</div>

      <div v-else class="overflow-auto">
        <table class="w-full border-collapse">
          <thead>
            <tr>
              <th v-for="col in columnsSupp" :key="col.key" @click="toggleSortSupp(col.key)" class="sticky top-0 bg-surface-gray text-left py-2 px-2 border-b border-border-gray cursor-pointer select-none whitespace-nowrap hover:bg-gray-100">
                <span>{{ col.label }}</span>
                <span class="ml-[0.35rem] text-xs text-muted" aria-hidden="true" v-if="sortKeySupp === col.key">
                  {{ sortDirSupp === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in sortedRowsSupp" :key="row.id">
              <td class="py-2 px-2 border-b border-border-light">{{ row.id }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.prenom }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.nom }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.email }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ row.partsSupplementaires }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ (row.partsSupplementaires || 0) * 10 }} €</td>
              <td class="py-2 px-2 border-b border-border-light">
                <span
                  class="inline-block py-1 px-2 rounded-sm text-sm font-semibold leading-[1.2]"
                  :class="{
                    'bg-status-pending-bg text-status-pending-text': row.status === 'PAYMENT_PENDING',
                    'bg-status-paid-bg text-status-paid-text': row.status === 'PAID',
                    'bg-status-processed-bg text-status-processed-text': row.status === 'PROCESSED',
                    'bg-status-archived-bg text-status-archived-text': row.status === 'ARCHIVED'
                  }"
                >{{ format(row.status) }}</span>
              </td>
              <td class="py-2 px-2 border-b border-border-light">{{ formatDate(row.createdAt) }}</td>
              <td class="py-2 px-2 border-b border-border-light">{{ formatDate(row.updatedAt) }}</td>
              <td class="py-2 px-2 border-b border-border-light">
                <button
                  v-if="row.status === 'PAYMENT_PENDING'"
                  class="bg-action-orange text-white border-none py-[0.35rem] px-3 rounded cursor-pointer text-sm font-medium whitespace-nowrap hover:bg-action-orange-hover"
                  @click="copyRetryLinkSupp(row)"
                >
                  {{ copiedIdSupp === row.id ? 'Copie !' : 'Copier lien' }}
                </button>
                <button
                  v-if="row.status === 'PAYMENT_PENDING'"
                  class="bg-action-gray text-white border-none py-[0.35rem] px-3 rounded cursor-pointer text-sm font-medium whitespace-nowrap ml-1 hover:bg-action-gray-hover disabled:opacity-60 disabled:cursor-not-allowed"
                  @click="archiveSouscriptionSupplementaire(row)"
                  :disabled="archivingSupp === row.id"
                >
                  {{ archivingSupp === row.id ? 'En cours...' : 'Archiver' }}
                </button>
                <button
                  v-if="row.status === 'PAID'"
                  class="bg-action-green text-white border-none py-[0.35rem] px-3 rounded cursor-pointer text-sm font-medium whitespace-nowrap hover:bg-action-green-hover disabled:opacity-60 disabled:cursor-not-allowed"
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
        <div class="flex items-center justify-center gap-2 py-4 border-t border-border-gray mt-4" v-if="totalPagesSupp > 1">
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="goToPageSupp(0)" :disabled="currentPageSupp === 0">
            &laquo;
          </button>
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="goToPageSupp(currentPageSupp - 1)" :disabled="currentPageSupp === 0">
            &lsaquo;
          </button>
          <span class="px-4 text-muted text-sm">
            Page {{ currentPageSupp + 1 }} sur {{ totalPagesSupp }}
          </span>
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="goToPageSupp(currentPageSupp + 1)" :disabled="currentPageSupp >= totalPagesSupp - 1">
            &rsaquo;
          </button>
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="goToPageSupp(totalPagesSupp - 1)" :disabled="currentPageSupp >= totalPagesSupp - 1">
            &raquo;
          </button>
          <select :value="pageSizeSupp" @change="changePageSizeSupp(Number(($event.target as HTMLSelectElement).value))" class="py-2 px-2 border border-border-gray rounded bg-white text-sm cursor-pointer ml-4">
            <option :value="10">10 / page</option>
            <option :value="20">20 / page</option>
            <option :value="50">50 / page</option>
            <option :value="100">100 / page</option>
          </select>
        </div>
      </div>
    </section>

    <!-- Binome Modal -->
    <div v-if="selectedBinome" class="fixed inset-0 bg-black/50 flex items-center justify-center z-[1000]" @click.self="closeBinomeModal">
      <div class="bg-white rounded-section w-[90%] max-w-[480px] max-h-[90vh] overflow-auto shadow-modal">
        <div class="flex items-center justify-between p-4 px-5 border-b border-border-gray">
          <h2 class="m-0 text-[1.125rem] font-semibold">Information du binôme</h2>
          <button class="bg-transparent border-none text-2xl leading-none text-muted cursor-pointer hover:text-text" @click="closeBinomeModal">&times;</button>
        </div>
        <div class="p-5">
          <dl class="m-0">
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Genre</dt>
              <dd class="m-0 text-text">{{ format(selectedBinome.genre) }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Prénom</dt>
              <dd class="m-0 text-text">{{ selectedBinome.prenom }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Nom</dt>
              <dd class="m-0 text-text">{{ selectedBinome.nom }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Date de naissance</dt>
              <dd class="m-0 text-text">{{ selectedBinome.dateNaissance || '—' }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Téléphone</dt>
              <dd class="m-0 text-text">{{ selectedBinome.telephone || '—' }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Email</dt>
              <dd class="m-0 text-text">{{ selectedBinome.email || '—' }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Adresse</dt>
              <dd class="m-0 text-text">{{ selectedBinome.adresse || '—' }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Ville</dt>
              <dd class="m-0 text-text">{{ selectedBinome.ville || '—' }}</dd>
            </div>
            <div class="flex py-2">
              <dt class="flex-[0_0_140px] font-medium text-muted">Code postal</dt>
              <dd class="m-0 text-text">{{ selectedBinome.codePostal || '—' }}</dd>
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
