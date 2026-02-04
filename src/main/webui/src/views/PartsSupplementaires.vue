<template>
  <main class="bg-bg-alt max-w-[980px] mx-auto py-10 px-4 pb-[3.25rem] text-text rounded-[10px]">
    <header class="bg-transparent rounded-[10px] py-6 px-5 mb-4 relative overflow-hidden">
      <div class="flex items-center gap-4 md:flex-row flex-col md:items-center items-start md:gap-4 gap-2">
        <img src="/superquinquin_logo_deule.svg" alt="SuperQuinquin sur Deûle" class="block h-24" />
        <div class="flex flex-col">
          <h1 class="m-0 mb-[0.35rem] text-3xl font-bold">Parts Supplémentaires</h1>
          <p class="text-sm m-0">Tu es déjà sociétaire et tu souhaites souscrire des parts supplémentaires.</p>
        </div>
      </div>
    </header>

    <form class="p-5 rounded-section" method="post" action="/api/v1/parts-supplementaires">
      <section>
        <h2 class="font-semibold m-0 mb-[0.9rem] text-text inline-flex items-center gap-2 section-title-bar">Tes informations</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-y-[1.15rem] gap-x-4">
          <div class="flex flex-col">
            <label for="prenom" class="font-semibold mb-[0.35rem] text-text-dark">Prénom <span class="text-req">*</span></label>
            <input id="prenom" name="prenom" v-model.trim="form.prenom" required class="bg-surface-hover border border-border rounded-input py-3 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
          </div>

          <div class="flex flex-col">
            <label for="nom" class="font-semibold mb-[0.35rem] text-text-dark">Nom <span class="text-req">*</span></label>
            <input id="nom" name="nom" v-model.trim="form.nom" required class="bg-surface-hover border border-border rounded-input py-3 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
          </div>

          <div class="flex flex-col col-span-full">
            <label for="email" class="font-semibold mb-[0.35rem] text-text-dark">Email <span class="text-req">*</span></label>
            <input id="email" name="email" v-model.trim="form.email" type="email" placeholder="toi@exemple.fr" class="bg-surface-hover border border-border rounded-input py-3 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
            <small v-if="form.email && !isEmail(form.email)" class="text-error font-semibold">Email invalide</small>
          </div>
        </div>
      </section>

      <section class="mt-5">
        <h2 class="font-semibold m-0 mb-[0.9rem] text-text inline-flex items-center gap-2 section-title-bar">Parts supplémentaires <span class="text-req">*</span></h2>
        <p class="text-sm text-muted mb-3">Choisis le nombre de parts supplémentaires que tu souhaites souscrire.</p>

        <div class="relative p-[0.9rem] border border-border mb-[0.85rem] option-gradient shadow-option option-accent-bar">
          <div class="flex flex-col">
            <label for="partsSupplementaires" class="font-semibold mb-[0.35rem] text-text-dark">Nombre de parts supplémentaires (10€ chacune)</label>
            <div class="inline-flex items-center gap-2">
              <button type="button" class="w-[38px] h-[38px] min-w-[38px] min-h-[38px] rounded-full border-none bg-accent text-accent-contrast font-extrabold text-[1.1rem] leading-none inline-flex items-center justify-center cursor-pointer shadow-stepper transition-all duration-200 hover:bg-accent-dark hover:-translate-y-px hover:shadow-stepper-hover active:translate-y-0" @click="decParts()">−</button>
              <input id="partsSupplementaires" name="partsSupplementaires" v-model.number="form.partsSupplementaires" type="number" min="1" step="1" class="w-[120px] text-center stepper-input bg-surface-hover border border-border rounded-input py-3 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
              <button type="button" class="w-[38px] h-[38px] min-w-[38px] min-h-[38px] rounded-full border-none bg-accent text-accent-contrast font-extrabold text-[1.1rem] leading-none inline-flex items-center justify-center cursor-pointer shadow-stepper transition-all duration-200 hover:bg-accent-dark hover:-translate-y-px hover:shadow-stepper-hover active:translate-y-0" @click="incParts()">+</button>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-[0.85rem]">
          <div class="flex flex-col">
            <label for="nbParts" class="font-semibold mb-[0.35rem] text-text-dark">Nombre total de parts</label>
            <input id="nbParts" type="number" :value="form.partsSupplementaires" readonly class="stepper-input bg-surface-light border border-border rounded-input py-3 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
          </div>
          <div class="flex flex-col">
            <label for="mtTotal" class="font-semibold mb-[0.35rem] text-text-dark">Montant total en euros</label>
            <input id="mtTotal" :value="totalAmount.toFixed(2) + ' €'" readonly class="bg-surface-light border border-border rounded-input py-3 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
          </div>
        </div>
      </section>

      <footer class="flex justify-between items-center gap-4 mt-5 pt-4 border-t border-border md:flex-row flex-col md:items-center items-stretch">
        <div class="flex flex-col gap-[0.125rem] md:order-none order-2 md:items-start items-center">
          <span class="text-muted font-semibold text-[0.9rem]">Total à payer</span>
          <strong class="text-[1.35rem] text-text">{{ totalAmount.toFixed(2) }} €</strong>
        </div>
        <button type="submit" class="bg-accent text-accent-contrast border-none rounded-btn py-[0.9rem] px-[1.35rem] text-[1.05rem] font-extrabold shadow-btn transition-all duration-200 hover:bg-accent-dark hover:-translate-y-px hover:shadow-btn-hover active:translate-y-0 disabled:opacity-60 disabled:cursor-not-allowed disabled:shadow-none md:order-none order-1 md:w-auto w-full" :disabled="!isFormValid">Payer {{ totalAmount.toFixed(2) }} €</button>
      </footer>
    </form>
  </main>
</template>

<script setup lang="ts">
import {computed, reactive} from 'vue'

const form = reactive({
  prenom: '',
  nom: '',
  email: '',
  partsSupplementaires: 1 as number,
})

const totalAmount = computed(() => form.partsSupplementaires * 10)

function isEmail(v: string) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)
}

const isFormValid = computed(() => {
  return (
      form.nom.trim().length > 0 &&
      form.prenom.trim().length > 0 &&
      isEmail(form.email) &&
      form.partsSupplementaires >= 1
  )
})

function incParts() {
  const v = Number(form.partsSupplementaires || 1)
  form.partsSupplementaires = Number.isFinite(v) ? Math.floor(v) + 1 : 2
}

function decParts() {
  const v = Number(form.partsSupplementaires || 1)
  const next = Number.isFinite(v) ? Math.floor(v) - 1 : 1
  form.partsSupplementaires = next < 1 ? 1 : next
}
</script>
