<template>
  <main class="bg-bg-alt max-w-[980px] mx-auto py-10 px-4 pb-[3.25rem] text-text rounded-[10px]">
    <header class="bg-transparent rounded-[10px] py-6 px-5 mb-4 relative overflow-hidden">
      <div class="flex md:flex-row flex-col md:items-center items-start md:gap-4 gap-2">
        <img src="/superquinquin_logo_deule.svg" alt="SuperQuinquin sur Deûle" class="block h-24" />
        <div class="flex flex-col">
          <h1 class="m-0 mb-[0.35rem] text-[1.7rem] font-bold">Inscription à la Coopérative SuperQuinquin sur Deûle</h1>
          <p class="text-sm m-0">Deviens sociétaire et règle tes parts sociales en quelques minutes.</p>
        </div>
      </div>
    </header>

    <form class="p-5" method="post" action="/api/v1/registrations">
      <section>
        <h2 class="m-0 mb-[0.9rem] text-text font-bold inline-flex items-center gap-2 section-title-bar">Informations personnelles</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-y-[1.15rem] gap-x-4">
          <div class="flex flex-col col-span-full">
            <div class="flex gap-4 items-center flex-wrap">
              <label class="inline-flex gap-[0.6rem] font-semibold items-center cursor-pointer">
                <input type="radio" name="genre" value="MADAME" v-model="form.genre"/>
                <span>Madame</span>
              </label>
              <label class="inline-flex gap-[0.6rem] font-semibold items-center cursor-pointer">
                <input type="radio" name="genre" value="MONSIEUR" v-model="form.genre"/>
                <span>Monsieur</span>
              </label>
            </div>
          </div>

          <div class="flex flex-col">
            <label for="prenom" class="font-semibold mb-[0.35rem] text-text-dark">Prénom <span class="text-req">*</span></label>
            <input id="prenom" name="prenom" v-model.trim="form.prenom" required class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
          </div>

          <div class="flex flex-col">
            <label for="nom" class=" font-semibold mb-[0.35rem] text-text-dark">Nom <span class="text-req">*</span></label>
            <input id="nom" name="nom" v-model.trim="form.nom" required class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
          </div>

          <div class="flex flex-col col-span-full">
            <label for="adresse" class=" font-semibold mb-[0.35rem] text-text-dark">Adresse <span class="text-req">*</span></label>
            <input id="adresse" name="adresse" v-model.trim="form.adresse" required class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
          </div>

          <div class="flex flex-col">
            <label for="ville" class=" font-semibold mb-[0.35rem] text-text-dark">Ville <span class="text-req">*</span></label>
            <input id="ville" name="ville" v-model.trim="form.ville" required class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
          </div>

          <div class="flex flex-col">
            <label for="cp" class=" font-semibold mb-[0.35rem] text-text-dark">Code postal <span class="text-req">*</span></label>
            <input id="cp" name="codePostal" v-model.trim="form.codePostal" inputmode="numeric" maxlength="5" placeholder="59000" class="max-w-[180px] bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
            <small v-if="form.codePostal && !/^\d{5}$/.test(form.codePostal)" class="text-error font-semibold">Code postal invalide</small>
          </div>

          <div class="flex flex-col">
            <label for="email" class=" font-semibold mb-[0.35rem] text-text-dark">Email <span class="text-req">*</span></label>
            <input id="email" name="email" v-model.trim="form.email" type="email" placeholder="toi@exemple.fr" class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
            <small v-if="form.email && !isEmail(form.email)" class="text-error font-semibold">Email invalide</small>
          </div>

          <div class="flex flex-col">
            <label for="tel" class=" font-semibold mb-[0.35rem] text-text-dark">Téléphone <span class="text-req">*</span></label>
            <input id="tel" name="telephone" v-model.trim="form.telephone" placeholder="06 00 00 00 00" class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
            <small v-if="form.telephone && !isPhone(form.telephone)" class="text-error font-semibold">Téléphone invalide</small>
          </div>

          <div class="flex flex-col">
            <label for="dob" class=" font-semibold mb-[0.35rem] text-text-dark">Date de naissance <span class="text-req">*</span></label>
            <input id="dob" v-model="form.dateNaissance" type="date" class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
          </div>

          <div class="flex flex-col">
            <label for="foyer" class=" font-semibold mb-[0.35rem] text-text-dark">Nombre de personnes au foyer <span class="text-req">*</span></label>
            <div class="inline-flex items-center gap-2">
              <button type="button" class="w-[38px] h-[38px] min-w-[38px] min-h-[38px] rounded-full border-none bg-accent text-accent-contrast font-extrabold leading-none inline-flex items-center justify-center cursor-pointer shadow-stepper transition-all duration-200 hover:bg-accent-dark hover:-translate-y-px hover:shadow-stepper-hover active:translate-y-0" @click="decFoyer()">−</button>
              <input id="foyer" name="nombreDePersonnesDansLeFoyer" v-model.number="form.nbFoyer" type="number" min="1" step="1" class="w-[120px] text-center stepper-input bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
              <button type="button" class="w-[38px] h-[38px] min-w-[38px] min-h-[38px] rounded-full border-none bg-accent text-accent-contrast font-extrabold leading-none inline-flex items-center justify-center cursor-pointer shadow-stepper transition-all duration-200 hover:bg-accent-dark hover:-translate-y-px hover:shadow-stepper-hover active:translate-y-0" @click="incFoyer()">+</button>
            </div>
          </div>
        </div>
      </section>

      <section class="mt-5">
        <h2 class=" m-0 mb-[0.9rem] text-text font-bold  inline-flex items-center gap-2 section-title-bar">Parts sociales <span class="text-req">*</span></h2>
        <p class="text-sm mb-5">Tu peux sélectionner une ou plusieurs options.</p>

        <div class="relative p-[0.9rem] border border-border mb-[0.85rem] option-gradient shadow-option option-accent-bar">
          <label class="inline-flex gap-[0.6rem] items-center cursor-pointer">
            <input type="checkbox" v-model="form.parts.p100.checked"/>
            <span class=" font-bold">100€ et plus</span>
          </label>
          <p class="text-sm my-1 mb-[0.6rem] leading-[1.45]">
            Je déclare vouloir devenir sociétaire et souscrire des parts sociales pour un montant minimum de 100 euros
            (au moins 10 parts à 10 euros).
          </p>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-[0.85rem]" v-if="form.parts.p100.checked">
            <div class="flex flex-col col-span-full">
              <small class="text-muted">Cette option ajoute automatiquement 10 parts (soit 100€) au minimum requis.</small>
            </div>
          </div>
        </div>

        <div class="relative p-[0.9rem] border border-border mb-[0.85rem] option-gradient shadow-option option-accent-bar">
          <label class="inline-flex gap-[0.6rem] items-center cursor-pointer">
            <input type="checkbox" v-model="form.parts.p10.checked"/>
            <span class=" font-bold">À partir de 10€</span>
          </label>
          <p class="text-sm my-1 mb-[0.6rem]  leading-[1.45]">
            Je déclare vouloir devenir sociétaire de la Coopérative SuperQuinquin sur Deûle et souscrire des parts pour
            un montant minimum de 10 euros. Ouvert aux étudiants et aux bénéficiaires des minimas sociaux. *
          </p>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-[0.85rem]" v-if="form.parts.p10.checked">
            <div class="flex flex-col col-span-full">
              <small class="text-muted">Cette option ajoute automatiquement 1 part (soit 10€) au minimum requis.</small>
            </div>
          </div>
        </div>

        <div class="relative p-[0.9rem] border border-border mb-[0.85rem] option-gradient shadow-option option-accent-bar">
          <label class="inline-flex gap-[0.6rem] items-center cursor-pointer">
            <input type="checkbox" v-model="form.binome.enabled"/>
            <span class=" font-bold">Ajouter un binôme</span>
          </label>
          <p class="text-sm my-1 mb-[0.6rem]  leading-[1.45]">Si tu ajoutes un binôme, 20€ seront automatiquement ajoutés à ta souscription.</p>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-[0.85rem]" v-if="form.binome.enabled">
            <div class="flex flex-col col-span-full">
              <div class="flex gap-4 items-center flex-wrap">
                <label class="text-sm font-semibold inline-flex gap-[0.6rem] items-center cursor-pointer">
                  <input type="radio" name="binomeGenre" value="MADAME" v-model="form.binome.genre"/>
                  <span>Madame</span>
                </label>
                <label class="text-sm font-semibold inline-flex gap-[0.6rem] items-center cursor-pointer">
                  <input type="radio" name="binomeGenre" value="MONSIEUR" v-model="form.binome.genre"/>
                  <span>Monsieur</span>
                </label>
              </div>
            </div>
            <div class="flex flex-col">
              <label for="bNom" class="text-sm font-semibold mb-[0.35rem] text-text-dark">Nom du binôme <span class="text-req">*</span></label>
              <input id="bNom" v-model.trim="form.binome.nom" class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
            </div>
            <div class="flex flex-col">
              <label for="bPrenom" class="text-sm font-semibold mb-[0.35rem] text-text-dark">Prénom du binôme <span class="text-req">*</span></label>
              <input id="bPrenom" v-model.trim="form.binome.prenom" class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
            </div>
            <div class="flex flex-col col-span-full">
              <label for="bAdresse" class="text-sm font-semibold mb-[0.35rem] text-text-dark">Adresse du binôme <span class="text-req">*</span></label>
              <input id="bAdresse" v-model.trim="form.binome.adresse" class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
            </div>
            <div class="flex flex-col">
              <label for="bVille" class="text-sm font-semibold mb-[0.35rem] text-text-dark">Ville du binôme <span class="text-req">*</span></label>
              <input id="bVille" v-model.trim="form.binome.ville" class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
            </div>
            <div class="flex flex-col">
              <label for="bCp" class="text-sm font-semibold mb-[0.35rem] text-text-dark">Code postal du binôme <span class="text-req">*</span></label>
              <input id="bCp" v-model.trim="form.binome.codePostal" inputmode="numeric" maxlength="5" placeholder="59000" class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
              <small v-if="form.binome.codePostal && !/^\d{5}$/.test(form.binome.codePostal)" class="text-error font-semibold">Code postal invalide</small>
            </div>
            <div class="flex flex-col col-span-full">
              <label for="bEmail" class="text-sm font-semibold mb-[0.35rem] text-text-dark">Email du binôme <span class="text-req">*</span></label>
              <input id="bEmail" v-model.trim="form.binome.email" type="email" placeholder="binome@exemple.fr" class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
              <small v-if="form.binome.email && !isEmail(form.binome.email)" class="text-error font-semibold">Email invalide</small>
            </div>
            <div class="flex flex-col">
              <label for="bTel" class="text-sm font-semibold mb-[0.35rem] text-text-dark">Téléphone du binôme <span class="text-req">*</span></label>
              <input id="bTel" v-model.trim="form.binome.telephone" placeholder="06 00 00 00 00" class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
              <small v-if="form.binome.telephone && !isPhone(form.binome.telephone)" class="text-error font-semibold">Téléphone invalide</small>
            </div>
            <div class="flex flex-col">
              <label for="bDob" class="text-sm font-semibold mb-[0.35rem] text-text-dark">Date de naissance du binôme <span class="text-req">*</span></label>
              <input id="bDob" v-model="form.binome.dateNaissance" type="date" class="bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
            </div>
            <div class="flex flex-col col-span-full">
              <small class="text-muted">L'option binôme ajoute automatiquement 2 parts (soit 20€) au minimum requis.</small>
            </div>
          </div>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-[0.85rem]">
          <div class="flex flex-col">
            <label for="nbParts" class="font-semibold mb-[0.35rem]  text-text-dark">Nombre total de parts à 10€</label>
            <input id="nbParts" type="number" :value="totalParts" readonly class="stepper-input bg-surface-light border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
          </div>
          <div class="flex flex-col">
            <label for="mtTotal" class="font-semibold mb-[0.35rem]  text-text-dark">Montant total en euros</label>
            <input id="mtTotal" :value="totalAmount.toFixed(2) + ' €'" readonly class="bg-surface-light border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
          </div>
        </div>
      </section>

      <section class="text-sm mt-5">
        <span>En devenant membre, je m'engage à participer au fonctionnement de la coopérative (coopérateurs actifs) à hauteur de 2h45 toutes les 4 semaines.</span>
      </section>

      <section class="mt-5">
        <label class="text-sm inline-flex gap-3 items-start cursor-pointer">
          <input type="checkbox" v-model="form.accepteStatuts" class="mt-[0.35rem]"/>
          <span>
            Je déclare avoir pris connaissance et accepter les <a class="underline" href="https://www.superquinquin.fr/wp-content/uploads/2025/12/Statuts_signes_superQuinquinsurDeule.pdf" target="_blank">Statuts de la coopérative SuperQuinquin sur Deûle.</a> Je déclare avoir bien compris que mes parts sociales sont un investissement soumis à un risque de perte en capital en cas de déficit de la coopérative. Je déclare ne violer aucune norme ou réglementation en souscrivant les parts sociales de la Coopérative.
          </span>
        </label>
      </section>

      <section class="mt-5">
        <div class="bg-transparent border-4 border-accent rounded-section p-[0.9rem]">
          <label class="inline-flex gap-[0.6rem] items-center cursor-pointer">
            <input type="checkbox" v-model="form.parts.soutien.checked"/>
            <span class="font-bold">Soutenir SuperQuinQuin</span>
          </label>
          <p class="text-sm my-1 mb-[0.6rem] leading-[1.45]">
            Je veux soutenir SuperQuinquin en souscrivant des parts supplémentaires
          </p>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-[0.85rem]" v-if="form.parts.soutien.checked">
            <div class="flex flex-col">
              <label for="soutienParts" class="font-semibold mb-[0.35rem] text-text-dark">Nombre de parts supplémentaires</label>
              <div class="inline-flex items-center gap-2">
                <button type="button" class="w-[38px] h-[38px] min-w-[38px] min-h-[38px] rounded-full border-none bg-white text-accent-contrast font-extrabold leading-none inline-flex items-center justify-center cursor-pointer shadow-stepper transition-all duration-200 hover:bg-white hover:-translate-y-px hover:shadow-stepper-hover active:translate-y-0" @click="decSoutien()">−</button>
                <input id="soutienParts" type="number" min="0" step="1" v-model.number="form.parts.soutien.parts" class="w-[120px] text-center stepper-input bg-surface-hover border border-border rounded-input py-2.5 px-[0.9rem] text-base transition-all duration-200 hover:bg-white input-focus-accent"/>
                <button type="button" class="w-[38px] h-[38px] min-w-[38px] min-h-[38px] rounded-full border-none bg-white text-accent-contrast font-extrabold leading-none inline-flex items-center justify-center cursor-pointer shadow-stepper transition-all duration-200 hover:bg-white hover:-translate-y-px hover:shadow-stepper-hover active:translate-y-0" @click="incSoutien()">+</button>
              </div>
              <small class="text-muted">Soit {{ (form.parts.soutien.parts || 0) * 10 }} € supplémentaires</small>
            </div>
          </div>
        </div>
      </section>

      <input type="hidden" name="etudiantOuMinimasSociaux" :value="form.parts.p10.checked ? 'true' : 'false'"/>
      <input type="hidden" name="partsDeSoutien" :value="form.parts.soutien.checked ? String(form.parts.soutien.parts || 0) : '0'"/>
      <input type="hidden" name="acceptationDesStatus" :value="form.accepteStatuts ? 'true' : 'false'"/>
      <input type="hidden" name="binomeEnabled" :value="form.binome.enabled ? 'true' : 'false'"/>
      <input type="hidden" name="binomeGenre" :value="form.binome.genre"/>
      <input type="hidden" name="binomeNom" :value="form.binome.nom"/>
      <input type="hidden" name="binomePrenom" :value="form.binome.prenom"/>
      <input type="hidden" name="binomeAdresse" :value="form.binome.adresse"/>
      <input type="hidden" name="binomeVille" :value="form.binome.ville"/>
      <input type="hidden" name="binomeCodePostal" :value="form.binome.codePostal"/>
      <input type="hidden" name="binomeEmail" :value="form.binome.email"/>
      <input type="hidden" name="binomeTelephone" :value="form.binome.telephone"/>
      <input type="hidden" name="binomeDateNaissance" :value="form.binome.dateNaissance"/>
      <footer class="flex justify-between gap-4 mt-5 pt-4 border-t border-border md:flex-row flex-col md:items-center items-stretch">
        <div class="flex flex-col gap-[0.125rem] md:order-none order-2 md:items-start items-center">
          <span class="text-sm font-semibold">Total à payer</span>
          <strong class=" text-text">{{ totalAmount.toFixed(2) }} €</strong>
        </div>
        <button type="submit" class="bg-accent text-accent-contrast border-none rounded-btn py-3 px-4 font-extrabold shadow-btn transition-all duration-200 hover:bg-accent-dark hover:-translate-y-px hover:shadow-btn-hover active:translate-y-0 disabled:opacity-60 disabled:cursor-not-allowed disabled:shadow-none md:order-none order-1 md:w-auto w-full" :disabled="!isFormValid">Payer {{ totalAmount.toFixed(2) }} €</button>
      </footer>
    </form>
  </main>
</template>

<script setup lang="ts">
import {computed, reactive} from 'vue'
import type {Genre} from "../api/model";

const form = reactive({
  genre: '' as '' | Genre,
  nom: '',
  prenom: '',
  adresse: '',
  ville: '',
  codePostal: '',
  email: '',
  telephone: '',
  dateNaissance: '',
  nbFoyer: 1 as number | undefined,
  parts: {
    p100: {
      checked: false,
      parts: 10
    },
    p10: {
      checked: false,
      parts: 1
    },
    soutien: {
      checked: false,
      parts: 0
    }
  },
  binome: {
    enabled: false,
    genre: '' as '' | Genre,
    nom: '',
    prenom: '',
    adresse: '',
    ville: '',
    codePostal: '',
    email: '',
    telephone: '',
    dateNaissance: '',
    parts: 2
  },
  accepteStatuts: false,
})

function partsCount() {
  const p100parts = form.parts.p100.checked ? form.parts.p100.parts : 0
  const p10parts = form.parts.p10.checked ? form.parts.p10.parts : 0
  const binomeparts = form.binome.enabled ? form.binome.parts : 0
  const soutienparts = form.parts.soutien.checked ? form.parts.soutien.parts : 0


  return p100parts + p10parts + binomeparts + soutienparts;
}

const totalParts = computed(() => partsCount())

const totalAmount = computed(() => partsCount() * 10)

function isEmail(v: string) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)
}

function isPhone(v: string) {
  return /^[+]?([0-9]?[\s\-.]?){6,15}[0-9]$/.test(v)
}

function isPostalCode(v: string) {
  return /^\d{5}$/.test(v)
}

const isPartsValid = computed(() => form.parts.p100.checked || form.parts.p10.checked)

const isFormValid = computed(() => {
  const baseValid = (
      !!form.genre &&
      form.nom.trim().length > 0 &&
      form.prenom.trim().length > 0 &&
      form.adresse.trim().length > 0 &&
      form.ville.trim().length > 0 &&
      isPostalCode(form.codePostal) &&
      isEmail(form.email) &&
      isPhone(form.telephone) &&
      !!form.dateNaissance &&
      !!form.nbFoyer && form.nbFoyer > 0 &&
      isPartsValid.value &&
      form.accepteStatuts
  )

  if (!baseValid) return false

  if (!form.binome.enabled) return true

  return (
      !!form.binome.genre &&
      form.binome.nom.trim().length > 0 &&
      form.binome.prenom.trim().length > 0 &&
      form.binome.adresse.trim().length > 0 &&
      form.binome.ville.trim().length > 0 &&
      isPostalCode(form.binome.codePostal) &&
      isEmail(form.binome.email) &&
      isPhone(form.binome.telephone) &&
      !!form.binome.dateNaissance
  )
})


function incSoutien() {
  if (!form.parts.soutien.checked) form.parts.soutien.checked = true
  const v = Number(form.parts.soutien.parts || 0)
  form.parts.soutien.parts = Number.isFinite(v) ? Math.floor(v) + 1 : 1
}

function decSoutien() {
  if (!form.parts.soutien.checked) return
  const v = Number(form.parts.soutien.parts || 0)
  const next = Number.isFinite(v) ? Math.floor(v) - 1 : 0
  form.parts.soutien.parts = next < 0 ? 0 : next
}

function incFoyer() {
  const v = Number(form.nbFoyer || 1)
  form.nbFoyer = Number.isFinite(v) ? Math.floor(v) + 1 : 2
}

function decFoyer() {
  const v = Number(form.nbFoyer || 1)
  const next = Number.isFinite(v) ? Math.floor(v) - 1 : 1
  form.nbFoyer = next < 1 ? 1 : next
}
</script>
