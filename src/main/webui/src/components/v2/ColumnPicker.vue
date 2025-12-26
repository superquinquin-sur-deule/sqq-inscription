<template>
  <div class="column-picker" ref="pickerRef">
    <button class="picker-btn" @click="toggleOpen" :title="'Colonnes'">
      <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <rect x="3" y="3" width="7" height="7"></rect>
        <rect x="14" y="3" width="7" height="7"></rect>
        <rect x="3" y="14" width="7" height="7"></rect>
        <rect x="14" y="14" width="7" height="7"></rect>
      </svg>
      <span class="picker-label">Colonnes</span>
    </button>

    <div v-if="isOpen" class="dropdown">
      <div class="dropdown-header">
        <span class="dropdown-title">Afficher les colonnes</span>
        <button class="reset-btn" @click="resetToDefaults">Réinitialiser</button>
      </div>
      <div class="dropdown-body">
        <label
          v-for="col in allColumns"
          :key="col.key"
          class="column-option"
        >
          <input
            type="checkbox"
            :checked="visibleColumns.includes(col.key)"
            @change="toggleColumn(col.key)"
          />
          <span>{{ col.label }}</span>
        </label>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

export interface ColumnConfig {
  key: string
  label: string
  default: boolean
  sortable: boolean
}

const props = defineProps<{
  visibleColumns: string[]
}>()

const emit = defineEmits<{
  (e: 'update:visibleColumns', columns: string[]): void
}>()

const STORAGE_KEY = 'v2-admin-grid-columns'

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

const isOpen = ref(false)
const pickerRef = ref<HTMLElement | null>(null)

function toggleOpen() {
  isOpen.value = !isOpen.value
}

function toggleColumn(key: string) {
  const current = [...props.visibleColumns]
  const index = current.indexOf(key)
  if (index > -1) {
    current.splice(index, 1)
  } else {
    // Insert in the correct order based on allColumns
    const insertIndex = allColumns.findIndex(c => c.key === key)
    let newIndex = 0
    for (let i = 0; i < insertIndex && i < allColumns.length; i++) {
      const col = allColumns[i]
      if (col && current.includes(col.key)) {
        newIndex++
      }
    }
    current.splice(newIndex, 0, key)
  }
  emit('update:visibleColumns', current)
  saveToStorage(current)
}

function resetToDefaults() {
  const defaults = allColumns.filter(c => c.default).map(c => c.key)
  emit('update:visibleColumns', defaults)
  saveToStorage(defaults)
}

function saveToStorage(columns: string[]) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(columns))
  } catch {
    // localStorage not available
  }
}

function handleClickOutside(event: MouseEvent) {
  if (pickerRef.value && !pickerRef.value.contains(event.target as Node)) {
    isOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// Export for parent to use
defineExpose({
  allColumns,
  getDefaultColumns: () => allColumns.filter(c => c.default).map(c => c.key),
  loadFromStorage: () => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY)
      if (saved) {
        return JSON.parse(saved) as string[]
      }
    } catch {
      // localStorage not available
    }
    return null
  }
})
</script>

<style scoped>
.column-picker {
  position: relative;
}

.picker-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  color: #374151;
  transition: background-color 0.15s;
}

.picker-btn:hover {
  background: #e5e7eb;
}

.picker-label {
  font-weight: 500;
}

.dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 0.5rem;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  min-width: 220px;
  z-index: 50;
}

.dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.dropdown-title {
  font-weight: 600;
  font-size: 0.9rem;
  color: #374151;
}

.reset-btn {
  font-size: 0.8rem;
  color: #3b82f6;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
}

.reset-btn:hover {
  background: #eff6ff;
}

.dropdown-body {
  max-height: 320px;
  overflow-y: auto;
  padding: 0.5rem 0;
}

.column-option {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 1rem;
  cursor: pointer;
  font-size: 0.9rem;
  color: #374151;
  transition: background-color 0.1s;
}

.column-option:hover {
  background: #f9fafb;
}

.column-option input[type="checkbox"] {
  width: 1rem;
  height: 1rem;
  accent-color: #3b82f6;
}
</style>
