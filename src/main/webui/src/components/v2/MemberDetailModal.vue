<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="isOpen && member" class="modal-overlay" @click.self="close">
        <div class="modal-card" @keydown.esc="close" tabindex="-1" ref="modalRef">
          <!-- Header -->
          <div class="modal-header">
            <h2 class="modal-title">Détails du membre</h2>
            <button class="close-btn" @click="close" type="button" aria-label="Fermer">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M18 6 6 18"></path>
                <path d="m6 6 12 12"></path>
              </svg>
            </button>
          </div>

          <!-- Status Badges -->
          <div class="badges-row">
            <span class="badge" :class="typeClass">
              {{ member.memberType === 'BINOME' ? 'Binôme' : 'Principal' }}
            </span>
            <span class="badge" :class="memberStatusClass">
              {{ formatMemberStatus(member.memberStatus) }}
            </span>
            <span class="badge" :class="paymentStatusClass">
              {{ formatPaymentStatus(member.paymentStatus) }}
            </span>
            <span class="badge badge-neutral">
              {{ member.totalParts ?? 0 }} part(s)
            </span>
          </div>

          <!-- Content -->
          <div class="modal-body">
            <!-- Personal Information -->
            <section class="section">
              <h3 class="section-title">Informations personnelles</h3>
              <div class="field-grid">
                <div class="field">
                  <span class="field-label">Genre</span>
                  <span class="field-value">{{ formatGenre(member.genre) }}</span>
                </div>
                <div class="field">
                  <span class="field-label">Prénom</span>
                  <span class="field-value">{{ member.prenom }}</span>
                </div>
                <div class="field">
                  <span class="field-label">Nom</span>
                  <span class="field-value">{{ member.nom }}</span>
                </div>
                <div class="field">
                  <span class="field-label">Date naissance</span>
                  <span class="field-value">{{ formatDate(member.dateNaissance) }}</span>
                </div>
                <div class="field">
                  <span class="field-label">Email</span>
                  <span class="field-value">{{ member.email || '—' }}</span>
                </div>
                <div class="field">
                  <span class="field-label">Téléphone</span>
                  <span class="field-value">{{ member.telephone || '—' }}</span>
                </div>
              </div>
            </section>

            <!-- Address -->
            <section class="section">
              <h3 class="section-title">Adresse</h3>
              <div class="field-grid">
                <div class="field field-wide">
                  <span class="field-label">Adresse</span>
                  <span class="field-value">{{ member.adresse || '—' }}</span>
                </div>
                <div class="field">
                  <span class="field-label">Code postal</span>
                  <span class="field-value">{{ member.codePostal || '—' }}</span>
                </div>
                <div class="field">
                  <span class="field-label">Ville</span>
                  <span class="field-value">{{ member.ville || '—' }}</span>
                </div>
              </div>
            </section>

            <!-- Additional Info -->
            <section class="section">
              <h3 class="section-title">Informations complémentaires</h3>
              <div class="field-grid">
                <div class="field">
                  <span class="field-label">Tarif réduit</span>
                  <span class="field-value">{{ formatBoolean(member.etudiantOuMinimasSociaux) }}</span>
                </div>
                <div class="field">
                  <span class="field-label">Pers. foyer</span>
                  <span class="field-value">{{ member.nombreDePersonnesDansLeFoyer ?? '—' }}</span>
                </div>
                <div class="field">
                  <span class="field-label">Source</span>
                  <span class="field-value">{{ formatSource(member.source) }}</span>
                </div>
              </div>
            </section>

            <!-- Partner -->
            <section v-if="member.partnerId" class="section">
              <h3 class="section-title">Binôme</h3>
              <div class="field-grid">
                <div class="field field-wide">
                  <span class="field-label">Lié à</span>
                  <button class="partner-link" @click="navigateToPartner" type="button">
                    {{ member.partnerName }}
                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M5 12h14"></path>
                      <path d="m12 5 7 7-7 7"></path>
                    </svg>
                  </button>
                </div>
              </div>
            </section>

            <!-- Notes -->
            <section v-if="member.notes" class="section">
              <h3 class="section-title">Notes</h3>
              <div class="notes-content">{{ member.notes }}</div>
            </section>

            <!-- Timestamps -->
            <section class="section section-timestamps">
              <div class="timestamps">
                <span>Créé le {{ formatDateTime(member.createdAt) }}</span>
                <span>Modifié le {{ formatDateTime(member.updatedAt) }}</span>
              </div>
            </section>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

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

const props = defineProps<{
  member: CooperateurV2DTO | null
  isOpen: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'navigate-to-partner', partnerId: number): void
}>()

const modalRef = ref<HTMLElement | null>(null)

const typeClass = computed(() => {
  return props.member?.memberType === 'BINOME' ? 'badge-binome' : 'badge-principal'
})

const memberStatusClass = computed(() => {
  switch (props.member?.memberStatus) {
    case 'PROSPECT': return 'badge-prospect'
    case 'ACTIVE': return 'badge-active'
    case 'SUSPENDED': return 'badge-suspended'
    case 'DEPARTED': return 'badge-departed'
    default: return 'badge-neutral'
  }
})

const paymentStatusClass = computed(() => {
  switch (props.member?.paymentStatus) {
    case 'PENDING': return 'badge-pending'
    case 'PAID': return 'badge-paid'
    default: return 'badge-neutral'
  }
})

function formatGenre(genre?: string) {
  switch (genre) {
    case 'M': return 'Monsieur'
    case 'MME': return 'Madame'
    default: return genre ?? '—'
  }
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

function formatDate(v?: string) {
  if (!v) return '—'
  const date = new Date(v)
  return date.toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

function formatDateTime(v?: string) {
  if (!v) return '—'
  const date = new Date(v)
  return date.toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function close() {
  emit('close')
}

function navigateToPartner() {
  if (props.member?.partnerId) {
    emit('navigate-to-partner', props.member.partnerId)
  }
}

function handleKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape' && props.isOpen) {
    close()
  }
}

// Focus modal when opened for keyboard accessibility
watch(() => props.isOpen, (isOpen) => {
  if (isOpen) {
    setTimeout(() => {
      modalRef.value?.focus()
    }, 50)
  }
})

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
/* Modal Overlay */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

/* Modal Card */
.modal-card {
  background: white;
  border-radius: 12px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  outline: none;
}

/* Header */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.25rem;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  background: white;
  z-index: 10;
}

.modal-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

/* Badges Row */
.badges-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  padding: 1rem 1.25rem;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.85rem;
  font-weight: 600;
}

.badge-principal {
  background: #e5e7eb;
  color: #374151;
}

.badge-binome {
  background: #dbeafe;
  color: #1d4ed8;
}

.badge-prospect {
  background: #e0e7ff;
  color: #3730a3;
}

.badge-active {
  background: #dcfce7;
  color: #166534;
}

.badge-suspended {
  background: #ffedd5;
  color: #9a3412;
}

.badge-departed {
  background: #e5e7eb;
  color: #374151;
}

.badge-pending {
  background: #fef9c3;
  color: #854d0e;
}

.badge-paid {
  background: #dcfce7;
  color: #166534;
}

.badge-neutral {
  background: #f3f4f6;
  color: #6b7280;
}

/* Body */
.modal-body {
  padding: 0 1.25rem 1.25rem;
}

/* Sections */
.section {
  padding: 1rem 0;
  border-bottom: 1px solid #f3f4f6;
}

.section:last-child {
  border-bottom: none;
}

.section-title {
  font-size: 0.8rem;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 0.75rem;
}

/* Field Grid */
.field-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.75rem 1.5rem;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.field-wide {
  grid-column: 1 / -1;
}

.field-label {
  font-size: 0.8rem;
  color: #9ca3af;
}

.field-value {
  font-size: 0.95rem;
  color: #111827;
}

/* Partner Link */
.partner-link {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  background: none;
  border: none;
  color: #2563eb;
  cursor: pointer;
  font-size: 0.95rem;
  padding: 0;
  text-decoration: underline;
}

.partner-link:hover {
  color: #1d4ed8;
}

/* Notes */
.notes-content {
  font-size: 0.95rem;
  color: #374151;
  background: #f9fafb;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  white-space: pre-wrap;
  line-height: 1.5;
}

/* Timestamps */
.section-timestamps {
  padding-bottom: 0;
}

.timestamps {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  font-size: 0.8rem;
  color: #9ca3af;
}

/* Transitions */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}

.modal-enter-active .modal-card,
.modal-leave-active .modal-card {
  transition: transform 0.2s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-card,
.modal-leave-to .modal-card {
  transform: scale(0.95);
}

/* Responsive */
@media (max-width: 480px) {
  .field-grid {
    grid-template-columns: 1fr;
  }

  .badges-row {
    justify-content: center;
  }
}
</style>
