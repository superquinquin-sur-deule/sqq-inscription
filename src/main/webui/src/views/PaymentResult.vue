<template>
  <main class="bg-bg-alt max-w-[980px] mx-auto py-10 px-4 pb-[3.25rem] text-text rounded-[10px]">
    <header class="bg-transparent rounded-[10px] py-6 px-5 mb-4 relative overflow-hidden">
      <div class="flex items-center gap-4">
        <img src="/superquinquin_logo_deule.svg" alt="SuperQuinquin sur Deule" class="block h-24"/>
        <div class="flex flex-col">
          <h1 v-if="loading" class="m-0 mb-[0.35rem] text-[1.9rem]">Verification en cours...</h1>
          <template v-else-if="paymentSuccess">
            <h1 class="m-0 mb-[0.35rem] text-[1.9rem]">Merci pour ta souscription !</h1>
            <p class="text-sm text-muted m-0">{{ isSupplementaire
              ? 'Ton paiement a bien ete confirme, tes parts additionnelles ont ete ajoutees.'
              : 'Ton paiement a bien ete confirme, tu es maintenant officiellement un.e cooperateur.rice de SuperQuinquin sur Deule.'
            }}</p>
          </template>
          <template v-else>
            <h1 class="m-0 mb-[0.35rem] text-[1.9rem]">Finaliser ton paiement</h1>
            <p class="text-muted m-0">Ton paiement n'a pas abouti. Tu peux le relancer ici.</p>
          </template>
        </div>
      </div>
    </header>

    <section class="p-5 rounded-section" v-if="loading">
      <p class="text-muted">Verification du paiement en cours...</p>
    </section>

    <section class="p-5  rounded-section text-left" v-else-if="paymentSuccess">
      <div class="w-12 h-12 rounded-full inline-flex items-center justify-center bg-accent text-accent-contrast font-bold mb-3" aria-hidden="true">✓</div>
      <h2 class="text-[1.25rem] m-0 mb-3">{{ isSupplementaire ? 'Souscription finalisée' : 'Inscription finalisée' }}</h2>
      <p v-if="isSupplementaire">
        Nous venons de valider ta souscription de parts additionnelles. Tu recevras un email de confirmation
        avec un recapitulatif de ta souscription et de ton paiement.
      </p>
      <p v-else>
        Nous venons de valider ta souscription de parts sociales. Tu recevras un email de confirmation
        avec un recapitulatif de ton inscription et de ton paiement.
      </p>

      <div class="mt-4">
        <h3 class="m-0 mb-2 text-base">Et maintenant ?</h3>
        <ul v-if="isSupplementaire" class="list-disc m-0 pl-5 text-text">
          <li>Surveille ta boite mail pour le recapitulatif</li>
          <li>Continue a soutenir SuperQuinquin sur Deule !</li>
        </ul>
        <ul v-else class="list-disc m-0 pl-5 text-text">
          <li>Surveille ta boite mail, tu vas recevoir ton mail d'accueil parmi les coop'</li>
          <li>Rejoins le mouvement en construisant avec nous ton futur magasin</li>
        </ul>
      </div>

      <p class="text-sm mt-6 text-muted text-center">
        Besoin d'aide ? Ecris nous a
        <a href="mailto:contact@superquinquin.fr" class="underline">contact@superquinquin.fr</a>.
      </p>
    </section>

    <!-- Etat: Erreur fatale -->
    <section class="bg-surface border border-border p-5 shadow-card rounded-section text-center" v-else-if="error">
      <div class="w-12 h-12 rounded-full inline-flex items-center justify-center bg-error-bg text-error-red font-bold text-2xl mb-3" aria-hidden="true">!</div>
      <h2 class="text-[1.25rem] m-0 mb-2 text-error-red">{{ errorTitle }}</h2>
      <p>{{ errorMessage }}</p>
      <p class="mt-6 text-muted text-center">
        Besoin d'aide ? Contacte-nous a
        <a href="mailto:contact@superquinquin.fr" class="underline">contact@superquinquin.fr</a>.
      </p>
    </section>

    <section class="bg-surface border border-border p-5 shadow-card rounded-section" v-else-if="paymentInfo">
      <h2 class="text-[1.25rem] m-0 mb-3">Recapitulatif de ta souscription</h2>
      <div class="my-4 border border-border rounded-section overflow-hidden">
        <div class="flex justify-between py-3 px-4 border-b border-border">
          <span class="text-muted">Nom</span>
          <span class="text-text">{{ paymentInfo.prenom }} {{ paymentInfo.nom }}</span>
        </div>
        <div class="flex justify-between py-3 px-4 border-b border-border">
          <span class="text-muted">Email</span>
          <span class="text-text">{{ paymentInfo.email }}</span>
        </div>
        <div class="flex justify-between py-3 px-4 border-b border-border">
          <span class="text-muted">Parts sociales</span>
          <span class="text-text">{{ paymentInfo.parts }}</span>
        </div>
        <div class="flex justify-between py-3 px-4 bg-surface-gray font-semibold">
          <span class="text-muted">Montant a payer</span>
          <span class="text-text">{{ paymentInfo.montantTotal }} EUR</span>
        </div>
      </div>

      <form :action="retryUrl" method="post" class="mt-6">
        <button type="submit" class="inline-block py-3 px-6 rounded-section no-underline font-semibold text-base border-none cursor-pointer w-full bg-accent text-accent-contrast hover:bg-accent-dark disabled:opacity-60 disabled:cursor-not-allowed" :disabled="submitting">
          {{ submitting ? 'Redirection en cours...' : 'Payer ' + paymentInfo.montantTotal + ' EUR' }}
        </button>
      </form>

      <p class="mt-6 text-muted text-center">
        Besoin d'aide ? Contacte-nous a
        <a href="mailto:contact@superquinquin.fr" class="underline">contact@superquinquin.fr</a>.
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
