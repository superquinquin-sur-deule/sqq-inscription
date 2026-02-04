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
        <span class="inline-flex items-center justify-center min-w-[1.5rem] h-6 px-[0.4rem] bg-black/10 rounded-full text-sm font-bold" :class="{ 'bg-black/[0.08]': activeTab === 'inscriptions' }">{{ coopTable.totalElements.value }}</span>
      </button>
      <button
        class="py-3 px-5 border-none bg-tab-bg text-tab-text text-[0.95rem] font-semibold rounded-section cursor-pointer transition-all duration-200 flex items-center gap-2 hover:bg-tab-hover"
        :class="{ 'bg-white text-text shadow-tab-active': activeTab === 'supplementaires' }"
        @click="activeTab = 'supplementaires'"
      >
        Parts supplémentaires
        <span class="inline-flex items-center justify-center min-w-[1.5rem] h-6 px-[0.4rem] bg-black/10 rounded-full text-sm font-bold" :class="{ 'bg-black/[0.08]': activeTab === 'supplementaires' }">{{ suppTable.totalElements.value }}</span>
      </button>
    </div>

    <section class="bg-white border border-border rounded-section p-4" v-show="activeTab === 'inscriptions'">
      <div class="flex items-center gap-3 mb-3 flex-wrap">
        <input
          v-model="coopTable.query.value"
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
              @change="loadCooperateurs(0, coopTable.pageSize.value)"
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
        <span class="text-muted text-sm" v-if="!coopTable.loading.value && !coopTable.error.value">{{ coopTable.totalElements.value }} résultat(s)</span>
      </div>

      <div v-if="coopTable.loading.value" class="p-4 text-tab-text">Chargement…</div>
      <div v-else-if="coopTable.error.value" class="p-4 text-red-700">{{ coopTable.error.value }}</div>

      <div v-else class="overflow-auto">
        <table class="w-full border-collapse">
          <thead>
            <tr>
              <th v-for="col in columns" :key="col.key" @click="coopTable.toggleSort(col.key)" class="sticky top-0 bg-surface-gray text-left py-2 px-2 border-b border-border-gray cursor-pointer select-none whitespace-nowrap hover:bg-gray-100">
                <span>{{ col.label }}</span>
                <span class="ml-[0.35rem] text-xs text-muted" aria-hidden="true" v-if="coopTable.sortKey.value === col.key">
                  {{ coopTable.sortDir.value === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in coopTable.paginatedRows.value" :key="row.id">
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
                    @click="binomeModal.open(row.binome)"
                >
                  Oui
                </button>
                <span v-else class="text-gray-400">Non</span>
              </td>
              <td class="py-2 px-2 border-b border-border-light">
                <span
                  class="status inline-block py-1 px-2 rounded-sm text-sm font-semibold leading-[1.2]"
                  :class="[{
                    'bg-status-pending-bg text-status-pending-text': row.status === 'PAYMENT_PENDING',
                    'bg-status-paid-bg text-status-paid-text': row.status === 'PAID',
                    'bg-status-processed-bg text-status-processed-text': row.status === 'PROCESSED',
                    'bg-status-archived-bg text-status-archived-text': row.status === 'ARCHIVED'
                  }, statusClass(row.status)]"
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
        <div class="flex items-center justify-center gap-2 py-4 border-t border-border-gray mt-4" v-if="coopTable.totalPages.value > 1">
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="coopTable.goToPage(0)" :disabled="coopTable.currentPage.value === 0">
            &laquo;
          </button>
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="coopTable.goToPage(coopTable.currentPage.value - 1)" :disabled="coopTable.currentPage.value === 0">
            &lsaquo;
          </button>
          <span class="px-4 text-muted text-sm">
            Page {{ coopTable.currentPage.value + 1 }} sur {{ coopTable.totalPages.value }}
          </span>
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="coopTable.goToPage(coopTable.currentPage.value + 1)" :disabled="coopTable.currentPage.value >= coopTable.totalPages.value - 1">
            &rsaquo;
          </button>
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="coopTable.goToPage(coopTable.totalPages.value - 1)" :disabled="coopTable.currentPage.value >= coopTable.totalPages.value - 1">
            &raquo;
          </button>
          <select :value="coopTable.pageSize.value" @change="coopTable.changePageSize(Number(($event.target as HTMLSelectElement).value))" class="py-2 px-2 border border-border-gray rounded bg-white text-sm cursor-pointer ml-4">
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
          v-model="suppTable.query.value"
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
              @change="loadSouscriptionsSupplementaires(0, suppTable.pageSize.value)"
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
        <span class="text-muted text-sm" v-if="!suppTable.loading.value && !suppTable.error.value">{{ suppTable.totalElements.value }} résultat(s)</span>
      </div>

      <div v-if="suppTable.loading.value" class="p-4 text-tab-text">Chargement…</div>
      <div v-else-if="suppTable.error.value" class="p-4 text-red-700">{{ suppTable.error.value }}</div>

      <div v-else class="overflow-auto">
        <table class="w-full border-collapse">
          <thead>
            <tr>
              <th v-for="col in columnsSupp" :key="col.key" @click="suppTable.toggleSort(col.key)" class="sticky top-0 bg-surface-gray text-left py-2 px-2 border-b border-border-gray cursor-pointer select-none whitespace-nowrap hover:bg-gray-100">
                <span>{{ col.label }}</span>
                <span class="ml-[0.35rem] text-xs text-muted" aria-hidden="true" v-if="suppTable.sortKey.value === col.key">
                  {{ suppTable.sortDir.value === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in suppTable.paginatedRows.value" :key="row.id">
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
        <div class="flex items-center justify-center gap-2 py-4 border-t border-border-gray mt-4" v-if="suppTable.totalPages.value > 1">
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="suppTable.goToPage(0)" :disabled="suppTable.currentPage.value === 0">
            &laquo;
          </button>
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="suppTable.goToPage(suppTable.currentPage.value - 1)" :disabled="suppTable.currentPage.value === 0">
            &lsaquo;
          </button>
          <span class="px-4 text-muted text-sm">
            Page {{ suppTable.currentPage.value + 1 }} sur {{ suppTable.totalPages.value }}
          </span>
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="suppTable.goToPage(suppTable.currentPage.value + 1)" :disabled="suppTable.currentPage.value >= suppTable.totalPages.value - 1">
            &rsaquo;
          </button>
          <button class="py-2 px-3 border border-border-gray bg-white rounded cursor-pointer text-sm transition-all duration-200 hover:bg-surface-gray hover:border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed" @click="suppTable.goToPage(suppTable.totalPages.value - 1)" :disabled="suppTable.currentPage.value >= suppTable.totalPages.value - 1">
            &raquo;
          </button>
          <select :value="suppTable.pageSize.value" @change="suppTable.changePageSize(Number(($event.target as HTMLSelectElement).value))" class="py-2 px-2 border border-border-gray rounded bg-white text-sm cursor-pointer ml-4">
            <option :value="10">10 / page</option>
            <option :value="20">20 / page</option>
            <option :value="50">50 / page</option>
            <option :value="100">100 / page</option>
          </select>
        </div>
      </div>
    </section>

    <!-- Binome Modal -->
    <div v-if="binomeModal.data.value" class="fixed inset-0 bg-black/50 flex items-center justify-center z-[1000]" @click.self="binomeModal.close">
      <div class="bg-white rounded-section w-[90%] max-w-[480px] max-h-[90vh] overflow-auto shadow-modal">
        <div class="flex items-center justify-between p-4 px-5 border-b border-border-gray">
          <h2 class="m-0 text-[1.125rem] font-semibold">Information du binôme</h2>
          <button class="bg-transparent border-none text-2xl leading-none text-muted cursor-pointer hover:text-text" @click="binomeModal.close">&times;</button>
        </div>
        <div class="p-5">
          <dl class="m-0">
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Genre</dt>
              <dd class="m-0 text-text">{{ format(binomeModal.data.value.genre) }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Prénom</dt>
              <dd class="m-0 text-text">{{ binomeModal.data.value.prenom }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Nom</dt>
              <dd class="m-0 text-text">{{ binomeModal.data.value.nom }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Date de naissance</dt>
              <dd class="m-0 text-text">{{ binomeModal.data.value.dateNaissance || '—' }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Téléphone</dt>
              <dd class="m-0 text-text">{{ binomeModal.data.value.telephone || '—' }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Email</dt>
              <dd class="m-0 text-text">{{ binomeModal.data.value.email || '—' }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Adresse</dt>
              <dd class="m-0 text-text">{{ binomeModal.data.value.adresse || '—' }}</dd>
            </div>
            <div class="flex py-2 border-b border-border-light">
              <dt class="flex-[0_0_140px] font-medium text-muted">Ville</dt>
              <dd class="m-0 text-text">{{ binomeModal.data.value.ville || '—' }}</dd>
            </div>
            <div class="flex py-2">
              <dt class="flex-[0_0_140px] font-medium text-muted">Code postal</dt>
              <dd class="m-0 text-text">{{ binomeModal.data.value.codePostal || '—' }}</dd>
            </div>
          </dl>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { CooperateurDTO, BinomeDTO, SouscriptionSupplementaireDTO, CooperateurStatus } from '../api/model'
import { getSqqInscriptionAPI } from '../api/service/catalog'
import { useTable, useFormatting, useModalState } from '../composables'

const api = getSqqInscriptionAPI()

// Formatting utilities
const { format, formatDate, yesNo } = useFormatting()

const statusClassMap: Record<string, string> = {
  PAYMENT_PENDING: 'pending',
  PAID: 'paid',
  PROCESSED: 'processed',
  ARCHIVED: 'archived',
}
function statusClass(status: string | undefined): string {
  return status ? (statusClassMap[status] ?? '') : ''
}

// Binome modal
const binomeModal = useModalState<BinomeDTO>()

// Action states
const processing = ref<number | null>(null)
const archiving = ref<number | null>(null)
const copiedId = ref<number | null>(null)
const processingSupp = ref<number | null>(null)
const archivingSupp = ref<number | null>(null)
const copiedIdSupp = ref<number | null>(null)

// Status filter state
const allStatuses: CooperateurStatus[] = ['PAYMENT_PENDING', 'PAID', 'PROCESSED', 'ARCHIVED']
const defaultStatuses: CooperateurStatus[] = ['PAYMENT_PENDING', 'PAID', 'PROCESSED']
const selectedStatuses = ref<CooperateurStatus[]>([...defaultStatuses])
const selectedStatusesSupp = ref<CooperateurStatus[]>([...defaultStatuses])

// Tabs
const activeTab = ref<'inscriptions' | 'supplementaires'>('inscriptions')

// Column definitions
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

// Tables using composable
const coopTable = useTable<CooperateurDTO, ColumnKey>(
  { columns, defaultSortKey: 'createdAt', defaultSortDir: 'desc' },
  ['nom', 'prenom', 'email']
)

const suppTable = useTable<SouscriptionSupplementaireDTO, ColumnKeySupp>(
  { columns: columnsSupp, defaultSortKey: 'createdAt', defaultSortDir: 'desc' },
  ['nom', 'prenom', 'email']
)

// Load cooperateurs with status filter
async function loadCooperateurs(_page = 0, size = 20) {
  try {
    coopTable.loading.value = true
    coopTable.error.value = null
    const resp = await api.getApiV1AdministrationCooperateurs({
      statuses: selectedStatuses.value.length > 0 ? selectedStatuses.value : undefined
    })
    const data = (resp as any).data ?? (resp as any)
    const allItems = Array.isArray(data) ? data : (Array.isArray(data.content) ? data.content : [])
    coopTable.setData(allItems)
    coopTable.pageSize.value = size
  } catch (e: any) {
    coopTable.error.value = e?.message ?? 'Une erreur est survenue lors du chargement.'
  } finally {
    coopTable.loading.value = false
  }
}

// Load souscriptions supplementaires with status filter
async function loadSouscriptionsSupplementaires(_page = 0, size = 20) {
  try {
    suppTable.loading.value = true
    suppTable.error.value = null
    const resp = await api.getApiV1AdministrationPartsAdditionnelles({
      statuses: selectedStatusesSupp.value.length > 0 ? selectedStatusesSupp.value : undefined
    })
    const data = (resp as any).data ?? (resp as any)
    const allItems = Array.isArray(data) ? data : (Array.isArray(data.content) ? data.content : [])
    suppTable.setData(allItems)
    suppTable.pageSize.value = size
  } catch (e: any) {
    suppTable.error.value = e?.message ?? 'Une erreur est survenue lors du chargement.'
  } finally {
    suppTable.loading.value = false
  }
}

async function markAsProcessed(row: CooperateurDTO) {
  if (!row.id) return
  processing.value = row.id
  try {
    const resp = await api.postApiV1AdministrationCooperateursIdProcess(row.id)
    const updated = (resp as any).data ?? resp
    const index = coopTable.allRows.value.findIndex(r => r.id === row.id)
    if (index !== -1) {
      coopTable.allRows.value[index] = updated
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
    const index = suppTable.allRows.value.findIndex(r => r.id === row.id)
    if (index !== -1) {
      suppTable.allRows.value[index] = updated
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
      coopTable.allRows.value = coopTable.allRows.value.filter(r => r.id !== row.id)
    } else {
      await loadCooperateurs(coopTable.currentPage.value, coopTable.pageSize.value)
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
      suppTable.allRows.value = suppTable.allRows.value.filter(r => r.id !== row.id)
    } else {
      await loadSouscriptionsSupplementaires(suppTable.currentPage.value, suppTable.pageSize.value)
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

onMounted(async () => {
  await loadCooperateurs(0, coopTable.pageSize.value)
  await loadSouscriptionsSupplementaires(0, suppTable.pageSize.value)
})
</script>
