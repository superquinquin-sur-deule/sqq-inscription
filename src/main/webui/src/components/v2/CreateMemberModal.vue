<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="isOpen" class="modal-overlay" @click.self="close">
        <div class="modal-card" @keydown.esc="close" tabindex="-1" ref="modalRef">
          <!-- Header -->
          <div class="modal-header">
            <h2 class="modal-title">Nouvelle inscription</h2>
            <button class="close-btn" @click="close" type="button" aria-label="Fermer">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M18 6 6 18"></path>
                <path d="m6 6 12 12"></path>
              </svg>
            </button>
          </div>

          <!-- Registration Type Selector -->
          <div class="type-selector">
            <button
              type="button"
              class="type-btn"
              :class="{ active: mode === 'solo' }"
              @click="mode = 'solo'"
            >
              Solo
            </button>
            <button
              type="button"
              class="type-btn"
              :class="{ active: mode === 'pair' }"
              @click="mode = 'pair'"
            >
              Binôme
            </button>
          </div>

          <!-- Form -->
          <form @submit.prevent="handleSubmit" class="modal-body">
            <!-- Error message -->
            <div v-if="errorMessage" class="error-banner">
              {{ errorMessage }}
            </div>

            <!-- Primary member section -->
            <section class="section">
              <h3 class="section-title">{{ mode === 'pair' ? 'Membre principal' : 'Informations personnelles' }}</h3>

              <div class="field-grid">
                <div class="field">
                  <label class="field-label">Genre *</label>
                  <select v-model="primary.genre" required class="input">
                    <option value="">Sélectionner...</option>
                    <option value="MADAME">Madame</option>
                    <option value="MONSIEUR">Monsieur</option>
                  </select>
                </div>

                <div class="field">
                  <label class="field-label">Prénom *</label>
                  <input v-model="primary.prenom" type="text" required class="input" />
                </div>

                <div class="field">
                  <label class="field-label">Nom *</label>
                  <input v-model="primary.nom" type="text" required class="input" />
                </div>

                <div class="field">
                  <label class="field-label">Date de naissance</label>
                  <input v-model="primary.dateNaissance" type="date" class="input" />
                </div>

                <div class="field">
                  <label class="field-label">Téléphone *</label>
                  <input v-model="primary.telephone" type="tel" required class="input" />
                </div>

                <div class="field">
                  <label class="field-label">Email *</label>
                  <input v-model="primary.email" type="email" required class="input" />
                </div>

                <div class="field field-wide">
                  <label class="field-label">Adresse *</label>
                  <input v-model="primary.adresse" type="text" required class="input" />
                </div>

                <div class="field">
                  <label class="field-label">Code postal *</label>
                  <input v-model="primary.codePostal" type="text" required class="input" />
                </div>

                <div class="field">
                  <label class="field-label">Ville *</label>
                  <input v-model="primary.ville" type="text" required class="input" />
                </div>

                <div class="field">
                  <label class="field-label">Personnes dans le foyer</label>
                  <input v-model.number="primary.nombreDePersonnesDansLeFoyer" type="number" min="1" class="input" />
                </div>

                <div class="field field-checkbox">
                  <label class="checkbox-label">
                    <input v-model="primary.etudiantOuMinimasSociaux" type="checkbox" />
                    <span>Tarif réduit (étudiant / minimas sociaux)</span>
                  </label>
                </div>

                <div class="field">
                  <label class="field-label">Parts de soutien</label>
                  <input v-model.number="primary.partsDeSoutien" type="number" min="0" class="input" placeholder="0" />
                </div>
              </div>
            </section>

            <!-- Binome section (only in pair mode) -->
            <section v-if="mode === 'pair'" class="section">
              <h3 class="section-title">Binôme</h3>

              <div class="field-grid">
                <div class="field">
                  <label class="field-label">Genre *</label>
                  <select v-model="binome.genre" required class="input">
                    <option value="">Sélectionner...</option>
                    <option value="MADAME">Madame</option>
                    <option value="MONSIEUR">Monsieur</option>
                  </select>
                </div>

                <div class="field">
                  <label class="field-label">Prénom *</label>
                  <input v-model="binome.prenom" type="text" required class="input" />
                </div>

                <div class="field">
                  <label class="field-label">Nom *</label>
                  <input v-model="binome.nom" type="text" required class="input" />
                </div>

                <div class="field">
                  <label class="field-label">Date de naissance</label>
                  <input v-model="binome.dateNaissance" type="date" class="input" />
                </div>

                <div class="field">
                  <label class="field-label">Téléphone *</label>
                  <input v-model="binome.telephone" type="tel" required class="input" />
                </div>

                <div class="field">
                  <label class="field-label">Email *</label>
                  <input v-model="binome.email" type="email" required class="input" />
                </div>

                <div class="field field-wide">
                  <label class="field-label">Adresse</label>
                  <input v-model="binome.adresse" type="text" class="input" placeholder="Identique au principal si vide" />
                </div>

                <div class="field">
                  <label class="field-label">Code postal</label>
                  <input v-model="binome.codePostal" type="text" class="input" placeholder="Identique au principal si vide" />
                </div>

                <div class="field">
                  <label class="field-label">Ville</label>
                  <input v-model="binome.ville" type="text" class="input" placeholder="Identique au principal si vide" />
                </div>
              </div>
            </section>

            <!-- Status section -->
            <section class="section">
              <h3 class="section-title">Statut</h3>

              <div class="field-grid">
                <div class="field">
                  <label class="field-label">Statut paiement</label>
                  <select v-model="primary.paymentStatus" class="input">
                    <option value="PAID">Payé</option>
                    <option value="PENDING">En attente</option>
                  </select>
                </div>

                <div class="field">
                  <label class="field-label">Statut membre</label>
                  <select v-model="primary.memberStatus" class="input">
                    <option value="ACTIVE">Actif</option>
                    <option value="PROSPECT">Prospect</option>
                    <option value="SUSPENDED">Suspendu</option>
                    <option value="DEPARTED">Parti</option>
                  </select>
                </div>

                <div class="field field-wide">
                  <label class="field-label">Notes</label>
                  <textarea v-model="primary.notes" class="input textarea" rows="2" placeholder="Notes administratives..."></textarea>
                </div>
              </div>
            </section>

            <!-- Submit -->
            <div class="actions">
              <button type="button" class="btn btn-secondary" @click="close" :disabled="submitting">
                Annuler
              </button>
              <button type="submit" class="btn btn-primary" :disabled="submitting">
                <span v-if="submitting">Enregistrement...</span>
                <span v-else>{{ mode === 'pair' ? 'Créer le binôme' : 'Créer le membre' }}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

type Mode = 'solo' | 'pair'

interface PrimaryForm {
  genre: string
  prenom: string
  nom: string
  dateNaissance: string
  telephone: string
  email: string
  adresse: string
  ville: string
  codePostal: string
  etudiantOuMinimasSociaux: boolean
  nombreDePersonnesDansLeFoyer: number | null
  partsDeSoutien: number | null
  paymentStatus: string
  memberStatus: string
  notes: string
}

interface BinomeForm {
  genre: string
  prenom: string
  nom: string
  dateNaissance: string
  telephone: string
  email: string
  adresse: string
  ville: string
  codePostal: string
  notes: string
}

const props = defineProps<{
  isOpen: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'created'): void
}>()

const modalRef = ref<HTMLElement | null>(null)
const mode = ref<Mode>('solo')
const submitting = ref(false)
const errorMessage = ref<string | null>(null)

const initialPrimary = (): PrimaryForm => ({
  genre: '',
  prenom: '',
  nom: '',
  dateNaissance: '',
  telephone: '',
  email: '',
  adresse: '',
  ville: '',
  codePostal: '',
  etudiantOuMinimasSociaux: false,
  nombreDePersonnesDansLeFoyer: null,
  partsDeSoutien: null,
  paymentStatus: 'PAID',
  memberStatus: 'ACTIVE',
  notes: '',
})

const initialBinome = (): BinomeForm => ({
  genre: '',
  prenom: '',
  nom: '',
  dateNaissance: '',
  telephone: '',
  email: '',
  adresse: '',
  ville: '',
  codePostal: '',
  notes: '',
})

const primary = ref<PrimaryForm>(initialPrimary())
const binome = ref<BinomeForm>(initialBinome())

function resetForm() {
  primary.value = initialPrimary()
  binome.value = initialBinome()
  mode.value = 'solo'
  errorMessage.value = null
}

function close() {
  emit('close')
}

async function handleSubmit() {
  submitting.value = true
  errorMessage.value = null

  try {
    if (mode.value === 'solo') {
      await axios.post('/api/v2/administration/cooperateurs/solo', {
        genre: primary.value.genre,
        prenom: primary.value.prenom,
        nom: primary.value.nom,
        dateNaissance: primary.value.dateNaissance || null,
        telephone: primary.value.telephone,
        email: primary.value.email,
        adresse: primary.value.adresse,
        ville: primary.value.ville,
        codePostal: primary.value.codePostal,
        etudiantOuMinimasSociaux: primary.value.etudiantOuMinimasSociaux,
        nombreDePersonnesDansLeFoyer: primary.value.nombreDePersonnesDansLeFoyer,
        partsDeSoutien: primary.value.partsDeSoutien,
        paymentStatus: primary.value.paymentStatus,
        memberStatus: primary.value.memberStatus,
        notes: primary.value.notes || null,
      })
    } else {
      await axios.post('/api/v2/administration/cooperateurs/pair', {
        principal: {
          genre: primary.value.genre,
          prenom: primary.value.prenom,
          nom: primary.value.nom,
          dateNaissance: primary.value.dateNaissance || null,
          telephone: primary.value.telephone,
          email: primary.value.email,
          adresse: primary.value.adresse,
          ville: primary.value.ville,
          codePostal: primary.value.codePostal,
          etudiantOuMinimasSociaux: primary.value.etudiantOuMinimasSociaux,
          nombreDePersonnesDansLeFoyer: primary.value.nombreDePersonnesDansLeFoyer,
          partsDeSoutien: primary.value.partsDeSoutien,
          paymentStatus: primary.value.paymentStatus,
          memberStatus: primary.value.memberStatus,
          notes: primary.value.notes || null,
        },
        binome: {
          genre: binome.value.genre,
          prenom: binome.value.prenom,
          nom: binome.value.nom,
          dateNaissance: binome.value.dateNaissance || null,
          telephone: binome.value.telephone,
          email: binome.value.email,
          adresse: binome.value.adresse || null,
          ville: binome.value.ville || null,
          codePostal: binome.value.codePostal || null,
          notes: binome.value.notes || null,
        },
      })
    }

    emit('created')
    close()
  } catch (e: any) {
    const msg = e?.response?.data?.message ?? e?.message ?? 'Une erreur est survenue.'
    errorMessage.value = msg
  } finally {
    submitting.value = false
  }
}

function handleKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape' && props.isOpen) {
    close()
  }
}

// Reset form when modal opens
watch(() => props.isOpen, (isOpen) => {
  if (isOpen) {
    resetForm()
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
  max-width: 700px;
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

/* Type Selector */
.type-selector {
  display: flex;
  padding: 1rem 1.25rem;
  gap: 0.5rem;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.type-btn {
  flex: 1;
  padding: 0.6rem 1rem;
  border: 2px solid #e5e7eb;
  background: white;
  border-radius: 8px;
  font-weight: 500;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.15s;
}

.type-btn:hover {
  border-color: #d1d5db;
}

.type-btn.active {
  border-color: #2563eb;
  background: #eff6ff;
  color: #1d4ed8;
}

/* Body */
.modal-body {
  padding: 0 1.25rem 1.25rem;
}

/* Error Banner */
.error-banner {
  margin: 1rem 0 0;
  padding: 0.75rem 1rem;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  color: #b91c1c;
  font-size: 0.9rem;
}

/* Sections */
.section {
  padding: 1rem 0;
  border-bottom: 1px solid #f3f4f6;
}

.section:last-of-type {
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
  gap: 0.75rem 1rem;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.field-wide {
  grid-column: 1 / -1;
}

.field-checkbox {
  grid-column: 1 / -1;
  flex-direction: row;
  align-items: center;
}

.field-label {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 500;
}

.input {
  padding: 0.5rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.95rem;
  color: #111827;
  background: white;
  transition: border-color 0.15s;
}

.input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.input::placeholder {
  color: #9ca3af;
}

.textarea {
  resize: vertical;
  min-height: 60px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #374151;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 1rem;
  height: 1rem;
  accent-color: #2563eb;
}

/* Actions */
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #e5e7eb;
}

.btn {
  padding: 0.6rem 1.25rem;
  border-radius: 6px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: white;
  border: 1px solid #d1d5db;
  color: #374151;
}

.btn-secondary:hover:not(:disabled) {
  background: #f9fafb;
}

.btn-primary {
  background: #2563eb;
  border: 1px solid #2563eb;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #1d4ed8;
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

  .type-selector {
    flex-direction: column;
  }
}
</style>
