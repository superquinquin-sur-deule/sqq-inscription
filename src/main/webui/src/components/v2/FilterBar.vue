<template>
  <div class="filter-bar">
    <!-- Global Search -->
    <div class="search-wrapper">
      <svg class="search-icon" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="11" cy="11" r="8"></circle>
        <path d="m21 21-4.3-4.3"></path>
      </svg>
      <input
        v-model="localSearch"
        type="text"
        placeholder="Rechercher (nom, email, téléphone...)"
        class="search-input"
        @input="handleSearchInput"
      />
      <button
        v-if="localSearch"
        class="clear-search"
        @click="clearSearch"
        type="button"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M18 6 6 18"></path>
          <path d="m6 6 12 12"></path>
        </svg>
      </button>
    </div>

    <!-- Type Filter -->
    <select v-model="localType" class="filter-select" @change="emitFilters">
      <option :value="null">Type: Tous</option>
      <option value="PRINCIPAL">Principal</option>
      <option value="BINOME">Binôme</option>
    </select>

    <!-- Member Status Filter -->
    <select v-model="localMemberStatus" class="filter-select" @change="emitFilters">
      <option :value="null">Statut membre: Tous</option>
      <option value="PROSPECT">Prospect</option>
      <option value="ACTIVE">Actif</option>
      <option value="SUSPENDED">Suspendu</option>
      <option value="DEPARTED">Parti</option>
    </select>

    <!-- Payment Status Filter -->
    <select v-model="localPaymentStatus" class="filter-select" @change="emitFilters">
      <option :value="null">Paiement: Tous</option>
      <option value="PENDING">En attente</option>
      <option value="PAID">Payé</option>
    </select>

    <!-- Source Filter -->
    <select v-model="localSource" class="filter-select" @change="emitFilters">
      <option :value="null">Source: Tous</option>
      <option value="ONLINE_REGISTRATION">Inscription en ligne</option>
      <option value="CSV_IMPORT">Import CSV</option>
      <option value="MANUAL_ENTRY">Saisie manuelle</option>
    </select>

    <!-- Date Range Filter -->
    <div class="date-filter" ref="dateFilterRef">
      <button class="date-btn" @click="toggleDateDropdown" type="button">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M8 2v4"></path>
          <path d="M16 2v4"></path>
          <rect width="18" height="18" x="3" y="4" rx="2"></rect>
          <path d="M3 10h18"></path>
        </svg>
        <span>{{ dateLabel }}</span>
      </button>
      <div v-if="showDateDropdown" class="date-dropdown">
        <button
          v-for="preset in datePresets"
          :key="preset.value"
          class="date-option"
          :class="{ active: datePreset === preset.value }"
          @click="selectDatePreset(preset.value)"
          type="button"
        >
          {{ preset.label }}
        </button>
        <div v-if="datePreset === 'custom'" class="custom-dates">
          <label>
            <span>Du:</span>
            <input type="date" v-model="localCreatedFrom" @change="emitFilters" />
          </label>
          <label>
            <span>Au:</span>
            <input type="date" v-model="localCreatedTo" @change="emitFilters" />
          </label>
        </div>
      </div>
    </div>

    <!-- Clear All -->
    <button
      v-if="hasActiveFilters"
      class="clear-all"
      @click="clearAll"
      type="button"
    >
      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M18 6 6 18"></path>
        <path d="m6 6 12 12"></path>
      </svg>
      <span>Effacer</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

export interface FilterState {
  search: string
  type: 'PRINCIPAL' | 'BINOME' | null
  memberStatus: 'PROSPECT' | 'ACTIVE' | 'SUSPENDED' | 'DEPARTED' | null
  paymentStatus: 'PENDING' | 'PAID' | null
  source: 'ONLINE_REGISTRATION' | 'CSV_IMPORT' | 'MANUAL_ENTRY' | null
  createdFrom: string | null
  createdTo: string | null
}

const props = defineProps<{
  modelValue: FilterState
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: FilterState): void
}>()

// Local state mirrors props for immediate UI feedback
const localSearch = ref(props.modelValue.search)
const localType = ref(props.modelValue.type)
const localMemberStatus = ref(props.modelValue.memberStatus)
const localPaymentStatus = ref(props.modelValue.paymentStatus)
const localSource = ref(props.modelValue.source)
const localCreatedFrom = ref(props.modelValue.createdFrom)
const localCreatedTo = ref(props.modelValue.createdTo)

// Date filter state
const showDateDropdown = ref(false)
const dateFilterRef = ref<HTMLElement | null>(null)
const datePreset = ref<string>('all')

const datePresets = [
  { value: 'all', label: 'Tous' },
  { value: 'today', label: "Aujourd'hui" },
  { value: '7days', label: '7 derniers jours' },
  { value: 'month', label: 'Ce mois' },
  { value: 'custom', label: 'Plage personnalisée...' },
]

// Debounce timer for search
let searchTimer: ReturnType<typeof setTimeout> | null = null

// Sync local state when props change
watch(() => props.modelValue, (newValue) => {
  localSearch.value = newValue.search
  localType.value = newValue.type
  localMemberStatus.value = newValue.memberStatus
  localPaymentStatus.value = newValue.paymentStatus
  localSource.value = newValue.source
  localCreatedFrom.value = newValue.createdFrom
  localCreatedTo.value = newValue.createdTo
}, { deep: true })

const dateLabel = computed(() => {
  if (!localCreatedFrom.value && !localCreatedTo.value) {
    return 'Date'
  }
  const preset = datePresets.find(p => p.value === datePreset.value)
  if (preset && datePreset.value !== 'all') {
    return preset.label
  }
  return 'Date'
})

const hasActiveFilters = computed(() => {
  return (
    localSearch.value ||
    localType.value !== null ||
    localMemberStatus.value !== null ||
    localPaymentStatus.value !== null ||
    localSource.value !== null ||
    localCreatedFrom.value !== null ||
    localCreatedTo.value !== null
  )
})

function handleSearchInput() {
  // Debounce search input
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  searchTimer = setTimeout(() => {
    emitFilters()
  }, 300)
}

function clearSearch() {
  localSearch.value = ''
  emitFilters()
}

function emitFilters() {
  emit('update:modelValue', {
    search: localSearch.value,
    type: localType.value,
    memberStatus: localMemberStatus.value,
    paymentStatus: localPaymentStatus.value,
    source: localSource.value,
    createdFrom: localCreatedFrom.value,
    createdTo: localCreatedTo.value,
  })
}

function toggleDateDropdown() {
  showDateDropdown.value = !showDateDropdown.value
}

function formatDateISO(date: Date): string {
  return date.toISOString().split('T')[0] ?? ''
}

function selectDatePreset(preset: string) {
  datePreset.value = preset
  const now = new Date()
  const today = formatDateISO(now)

  switch (preset) {
    case 'all':
      localCreatedFrom.value = null
      localCreatedTo.value = null
      showDateDropdown.value = false
      break
    case 'today':
      localCreatedFrom.value = today
      localCreatedTo.value = today
      showDateDropdown.value = false
      break
    case '7days': {
      const sevenDaysAgo = new Date(now)
      sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7)
      localCreatedFrom.value = formatDateISO(sevenDaysAgo)
      localCreatedTo.value = today
      showDateDropdown.value = false
      break
    }
    case 'month': {
      const firstOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
      localCreatedFrom.value = formatDateISO(firstOfMonth)
      localCreatedTo.value = today
      showDateDropdown.value = false
      break
    }
    case 'custom':
      // Keep dropdown open for custom date selection
      break
  }
  emitFilters()
}

function clearAll() {
  localSearch.value = ''
  localType.value = null
  localMemberStatus.value = null
  localPaymentStatus.value = null
  localSource.value = null
  localCreatedFrom.value = null
  localCreatedTo.value = null
  datePreset.value = 'all'
  emitFilters()
}

function handleClickOutside(event: MouseEvent) {
  if (dateFilterRef.value && !dateFilterRef.value.contains(event.target as Node)) {
    showDateDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
})
</script>

<style scoped>
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 0;
}

/* Search Input */
.search-wrapper {
  position: relative;
  flex: 1;
  min-width: 200px;
  max-width: 320px;
}

.search-icon {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 0.5rem 2rem 0.5rem 2.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.9rem;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.clear-search {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #9ca3af;
  cursor: pointer;
  padding: 0.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.clear-search:hover {
  color: #6b7280;
}

/* Select Dropdowns */
.filter-select {
  padding: 0.5rem 0.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  font-size: 0.875rem;
  color: #374151;
  cursor: pointer;
  min-width: 130px;
}

.filter-select:focus {
  outline: none;
  border-color: #3b82f6;
}

/* Date Filter */
.date-filter {
  position: relative;
}

.date-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.875rem;
  color: #374151;
}

.date-btn:hover {
  background: #f9fafb;
}

.date-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  margin-top: 0.5rem;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  min-width: 200px;
  z-index: 50;
  padding: 0.5rem 0;
}

.date-option {
  display: block;
  width: 100%;
  padding: 0.5rem 1rem;
  text-align: left;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
  color: #374151;
}

.date-option:hover {
  background: #f9fafb;
}

.date-option.active {
  background: #eff6ff;
  color: #2563eb;
  font-weight: 500;
}

.custom-dates {
  padding: 0.75rem 1rem;
  border-top: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.custom-dates label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #6b7280;
}

.custom-dates input[type="date"] {
  flex: 1;
  padding: 0.35rem 0.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  font-size: 0.85rem;
}

/* Clear All Button */
.clear-all {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.5rem 0.75rem;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.875rem;
  color: #dc2626;
  font-weight: 500;
}

.clear-all:hover {
  background: #fee2e2;
}
</style>
