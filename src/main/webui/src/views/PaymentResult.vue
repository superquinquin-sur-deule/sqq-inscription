<template>
  <main class="container">
    <header class="header">
      <div class="brand">
        <img src="/superquinquin_logo_deule.svg" alt="SuperQuinquin sur Deule" class="logo"/>
        <div class="brand-text">
          <h1 v-if="loading">Verification en cours...</h1>
          <template v-else-if="paymentSuccess">
            <h1>Merci pour ta souscription !</h1>
            <p class="subtitle">{{ isSupplementaire
              ? 'Ton paiement a bien ete confirme, tes parts additionnelles ont ete ajoutees.'
              : 'Ton paiement a bien ete confirme, tu es maintenant officiellement un.e cooperateur.rice de SuperQuinquin sur Deule.'
            }}</p>
          </template>
          <template v-else>
            <h1>Finaliser ton paiement</h1>
            <p class="subtitle">Ton paiement n'a pas abouti. Tu peux le relancer ici.</p>
          </template>
        </div>
      </div>
    </header>

    <section class="section" v-if="loading">
      <p class="state">Verification du paiement en cours...</p>
    </section>

    <section class="section success" v-else-if="paymentSuccess">
      <div class="success-icon" aria-hidden="true">✓</div>
      <h2>{{ isSupplementaire ? 'Souscription finalisee' : 'Inscription finalisee' }}</h2>
      <p v-if="isSupplementaire">
        Nous venons de valider ta souscription de parts additionnelles. Tu recevras un email de confirmation
        avec un recapitulatif de ta souscription et de ton paiement.
      </p>
      <p v-else>
        Nous venons de valider ta souscription de parts sociales. Tu recevras un email de confirmation
        avec un recapitulatif de ton inscription et de ton paiement.
      </p>

      <div class="next-steps">
        <h3>Et maintenant ?</h3>
        <ul v-if="isSupplementaire">
          <li>Surveille ta boite mail pour le recapitulatif</li>
          <li>Continue a soutenir SuperQuinquin sur Deule !</li>
        </ul>
        <ul v-else>
          <li>Surveille ta boite mail, tu vas recevoir ton mail d'accueil parmi les coop'</li>
          <li>Rejoins le mouvement en construisant avec nous ton futur magasin</li>
        </ul>
      </div>

      <p class="help">
        Besoin d'aide ? Ecris nous a
        <a href="mailto:contact@superquinquin.fr">contact@superquinquin.fr</a>.
      </p>
    </section>

    <!-- Etat: Erreur fatale -->
    <section class="section error-section" v-else-if="error">
      <div class="error-icon" aria-hidden="true">!</div>
      <h2>{{ errorTitle }}</h2>
      <p>{{ errorMessage }}</p>
      <p class="help">
        Besoin d'aide ? Contacte-nous a
        <a href="mailto:contact@superquinquin.fr">contact@superquinquin.fr</a>.
      </p>
    </section>

    <section class="section" v-else-if="paymentInfo">
      <h2>Recapitulatif de ta souscription</h2>
      <div class="recap">
        <div class="recap-row">
          <span class="recap-label">Nom</span>
          <span class="recap-value">{{ paymentInfo.prenom }} {{ paymentInfo.nom }}</span>
        </div>
        <div class="recap-row">
          <span class="recap-label">Email</span>
          <span class="recap-value">{{ paymentInfo.email }}</span>
        </div>
        <div class="recap-row">
          <span class="recap-label">Parts sociales</span>
          <span class="recap-value">{{ paymentInfo.parts }}</span>
        </div>
        <div class="recap-row total">
          <span class="recap-label">Montant a payer</span>
          <span class="recap-value">{{ paymentInfo.montantTotal }} EUR</span>
        </div>
      </div>

      <form :action="retryUrl" method="post" class="retry-form">
        <button type="submit" class="btn primary" :disabled="submitting">
          {{ submitting ? 'Redirection en cours...' : 'Payer ' + paymentInfo.montantTotal + ' EUR' }}
        </button>
      </form>

      <p class="help">
        Besoin d'aide ? Contacte-nous a
        <a href="mailto:contact@superquinquin.fr">contact@superquinquin.fr</a>.
      </p>
    </section>
  </main>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

interface PaymentInfo {
  type: string
  uuid: string
  prenom: string
  nom: string
  email: string
  parts: number
  montantTotal: number
}

const loading = ref(true)
const paymentSuccess = ref(false)
const error = ref(false)
const errorTitle = ref('')
const errorMessage = ref('')
const paymentInfo = ref<PaymentInfo | null>(null)
const submitting = ref(false)

const urlParams = new URLSearchParams(window.location.search)
const uuid = urlParams.get('uuid')
const type = urlParams.get('type')

const isSupplementaire = type === 'supplementaire'

const retryUrl = computed(() => {
  if (isSupplementaire) {
    return `/api/v1/retry-payment/souscription/${uuid}`
  }
  return `/api/v1/retry-payment/cooperateur/${uuid}`
})

onMounted(async () => {
  if (!uuid) {
    error.value = true
    errorTitle.value = 'Lien invalide'
    errorMessage.value = 'Le lien est invalide ou incomplet.'
    loading.value = false
    return
  }

  try {
    // Etape 1: Verifier le statut du paiement
    const successUrl = isSupplementaire
      ? `/api/v1/parts-supplementaires/success/${uuid}`
      : `/api/v1/registrations/success/${uuid}`

    const successResponse = await fetch(successUrl, { method: 'POST' })

    if (successResponse.ok) {
      // Paiement confirme
      paymentSuccess.value = true
      loading.value = false
      return
    }

    const retryInfoUrl = isSupplementaire
      ? `/api/v1/retry-payment/souscription/${uuid}`
      : `/api/v1/retry-payment/cooperateur/${uuid}`

    const retryResponse = await fetch(retryInfoUrl)

    if (retryResponse.status === 404) {
      error.value = true
      errorTitle.value = 'Souscription non trouvee'
      errorMessage.value = 'Nous n\'avons pas trouve de souscription correspondant a ce lien.'
      loading.value = false
      return
    }

    if (retryResponse.status === 400) {
      error.value = true
      errorTitle.value = 'Paiement deja effectue'
      errorMessage.value = 'Cette souscription a deja ete payee ou traitee. Aucune action n\'est necessaire.'
      loading.value = false
      return
    }

    if (!retryResponse.ok) {
      throw new Error('Erreur serveur')
    }

    paymentInfo.value = await retryResponse.json()
  } catch (e) {
    error.value = true
    errorTitle.value = 'Erreur'
    errorMessage.value = 'Une erreur est survenue lors du chargement des informations. Veuillez reessayer.'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
:root {
  --text: #1f2937;
  --muted: #6b7280;
  --bg: #f6f7fb;
  --surface: #ffffff;
  --border: #e6e8ee;
  --shadow: 0 10px 30px rgba(16, 24, 40, 0.06);
  --accent: #f1dc43;
  --accent-dark: #d8c237;
  --accent-contrast: #111827;
  --error: #dc2626;
  --error-bg: #fef2f2;
}

.container {
  background: #efefee;
  max-width: 980px;
  margin: 0 auto;
  padding: 2.5rem 1rem 3.25rem;
  color: var(--text);
  border-radius: 10px;
}

.header {
  background: transparent;
  border-radius: 10px;
  padding: 1.5rem 1.25rem;
  margin-bottom: 1rem;
  position: relative;
  overflow: hidden;
}

.brand {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logo {
  display: block;
  height: 96px;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.subtitle {
  color: var(--muted);
  margin: 0;
}

.section {
  background: var(--surface);
  border: 1px solid var(--border);
  padding: 1.25rem;
  box-shadow: var(--shadow);
  border-radius: 8px;
}

.state {
  color: var(--muted);
}

/* Succes */
.success {
  text-align: left;
}

.success h2 {
  font-size: 1.25rem;
  margin: 0 0 .75rem;
}

.success-icon {
  width: 48px;
  height: 48px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--accent);
  color: var(--accent-contrast);
  font-weight: 700;
  margin-bottom: .75rem;
}

.next-steps {
  margin-top: 1rem;
}

.next-steps h3 {
  margin: 0 0 .5rem;
  font-size: 1rem;
}

.next-steps ul {
  margin: 0;
  padding-left: 1.25rem;
  color: var(--text);
}

/* Erreur */
.error-section {
  text-align: center;
}

.error-icon {
  width: 48px;
  height: 48px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--error-bg);
  color: var(--error);
  font-weight: 700;
  font-size: 1.5rem;
  margin-bottom: .75rem;
}

.error-section h2 {
  font-size: 1.25rem;
  margin: 0 0 .5rem;
  color: var(--error);
}

/* Recap */
.recap {
  margin: 1rem 0;
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
}

.recap-row {
  display: flex;
  justify-content: space-between;
  padding: .75rem 1rem;
  border-bottom: 1px solid var(--border);
}

.recap-row:last-child {
  border-bottom: none;
}

.recap-row.total {
  background: #f9fafb;
  font-weight: 600;
}

.recap-label {
  color: var(--muted);
}

.recap-value {
  color: var(--text);
}

.retry-form {
  margin-top: 1.5rem;
}

.btn {
  display: inline-block;
  padding: .75rem 1.5rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  font-size: 1rem;
  border: none;
  cursor: pointer;
  width: 100%;
}

.btn.primary {
  background: var(--accent);
  color: var(--accent-contrast);
}

.btn.primary:focus,
.btn.primary:hover {
  background: var(--accent-dark);
}

.btn.primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.help {
  margin-top: 1.5rem;
  color: var(--muted);
  text-align: center;
}

h2 {
  font-size: 1.25rem;
  margin: 0 0 .75rem;
}
</style>
