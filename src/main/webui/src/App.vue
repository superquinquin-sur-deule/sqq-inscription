<template>
  <main class="container">
    <header class="header">
      <div class="brand">
        <img src="/superquinquin_logo_deule.svg" alt="SuperQuinquin sur Deûle" class="logo" />
        <div class="brand-text">
          <h1>Inscription à la Coopérative SuperQuinquin sur Deûle</h1>
          <p class="subtitle">Devenez sociétaire et réglez vos parts sociales en quelques minutes.</p>
        </div>
      </div>
    </header>

    <form class="form" @submit.prevent="submit">
      <section class="section">
        <h2>Informations personnelles</h2>
        <div class="grid">
          <div class="field full">
            <div class="inline">
              <label class="radio">
                <input type="radio" name="genre" value="madame" v-model="form.genre"/>
                <span>Madame</span>
              </label>
              <label class="radio">
                <input type="radio" name="genre" value="monsieur" v-model="form.genre"/>
                <span>Monsieur</span>
              </label>
            </div>
          </div>

          <div class="field">
            <label for="nom">Nom <span class="req">*</span></label>
            <input id="nom" v-model.trim="form.nom" required/>
          </div>

          <div class="field">
            <label for="prenom">Prénom <span class="req">*</span></label>
            <input id="prenom" v-model.trim="form.prenom" required/>
          </div>

          <div class="field full">
            <label for="adresse">Adresse <span class="req">*</span></label>
            <input id="adresse" v-model.trim="form.adresse" required/>
          </div>

          <div class="field">
            <label for="ville">Ville <span class="req">*</span></label>
            <input id="ville" v-model.trim="form.ville" required/>
          </div>

          <div class="field">
            <label for="cp">Code postal <span class="req">*</span></label>
            <input id="cp" v-model.trim="form.codePostal" inputmode="numeric" maxlength="5" placeholder="59000"/>
            <small v-if="form.codePostal && !/^\d{5}$/.test(form.codePostal)" class="error">Code postal invalide</small>
          </div>

          <div class="field">
            <label for="email">Email <span class="req">*</span></label>
            <input id="email" v-model.trim="form.email" type="email" placeholder="vous@exemple.fr"/>
            <small v-if="form.email && !isEmail(form.email)" class="error">Email invalide</small>
          </div>

          <div class="field">
            <label for="tel">Téléphone <span class="req">*</span></label>
            <input id="tel" v-model.trim="form.telephone" placeholder="06 00 00 00 00"/>
            <small v-if="form.telephone && !isPhone(form.telephone)" class="error">Téléphone invalide</small>
          </div>

          <div class="field">
            <label for="dob">Date de naissance <span class="req">*</span></label>
            <input id="dob" v-model="form.dateNaissance" type="date"/>
          </div>

          <div class="field">
            <label for="foyer">Nombre de personnes au foyer <span class="req">*</span></label>
            <div class="stepper">
              <button type="button" class="stepper-btn" @click="decFoyer()">−</button>
              <input id="foyer" v-model.number="form.nbFoyer" type="number" min="1" step="1"/>
              <button type="button" class="stepper-btn" @click="incFoyer()">+</button>
            </div>
          </div>
        </div>
      </section>

      <section class="section">
        <h2>Parts sociales <span class="req">*</span></h2>
        <p class="hint">Vous pouvez sélectionner une ou plusieurs options.</p>

        <div class="option">
          <label class="checkbox">
            <input type="checkbox" v-model="form.parts.p100.checked"/>
            <span class="title">100€ et plus</span>
          </label>
          <p class="details">
            Je déclare vouloir devenir sociétaire et souscrire des parts sociales pour un montant minimum de 100 euros
            (au moins 10 parts à 10 euros).
          </p>
          <div class="subgrid" v-if="form.parts.p100.checked">
            <div class="field full">
              <small class="hint">Cette option ajoute automatiquement 10 parts (soit 100€) au minimum requis.</small>
            </div>
          </div>
        </div>

        <div class="option">
          <label class="checkbox">
            <input type="checkbox" v-model="form.parts.p10.checked"/>
            <span class="title">À partir de 10€</span>
          </label>
          <p class="details">
            Je déclare vouloir devenir sociétaire de la Coopérative SuperQuinquin sur Deûle et souscrire des parts pour
            un montant minimum de 10 euros. Ouvert aux étudiants et aux bénéficiaires des minimas sociaux. *
          </p>
          <div class="subgrid" v-if="form.parts.p10.checked">
            <div class="field full">
              <small class="hint">Cette option ajoute automatiquement 1 part (soit 10€) au minimum requis.</small>
            </div>
          </div>
        </div>

        <div class="option">
          <label class="checkbox">
            <input type="checkbox" v-model="form.binome.enabled"/>
            <span class="title">Ajouter un binôme</span>
          </label>
          <p class="details">Si vous ajoutez un binôme, 20€ seront automatiquement ajoutés à votre souscription.</p>
          <div class="subgrid" v-if="form.binome.enabled">
            <div class="field">
              <label for="bNom">Nom du binôme <span class="req">*</span></label>
              <input id="bNom" v-model.trim="form.binome.nom"/>
            </div>
            <div class="field">
              <label for="bPrenom">Prénom du binôme <span class="req">*</span></label>
              <input id="bPrenom" v-model.trim="form.binome.prenom"/>
            </div>
            <div class="field full">
              <label for="bAdresse">Adresse du binôme <span class="req">*</span></label>
              <input id="bAdresse" v-model.trim="form.binome.adresse"/>
            </div>
            <div class="field">
              <label for="bVille">Ville du binôme <span class="req">*</span></label>
              <input id="bVille" v-model.trim="form.binome.ville"/>
            </div>
            <div class="field">
              <label for="bCp">Code postal du binôme <span class="req">*</span></label>
              <input id="bCp" v-model.trim="form.binome.codePostal" inputmode="numeric" maxlength="5" placeholder="59000"/>
              <small v-if="form.binome.codePostal && !/^\d{5}$/.test(form.binome.codePostal)" class="error">Code postal invalide</small>
            </div>
            <div class="field full">
              <label for="bEmail">Email du binôme <span class="req">*</span></label>
              <input id="bEmail" v-model.trim="form.binome.email" type="email" placeholder="binome@exemple.fr"/>
              <small v-if="form.binome.email && !isEmail(form.binome.email)" class="error">Email invalide</small>
            </div>
            <div class="field">
              <label for="bTel">Téléphone du binôme <span class="req">*</span></label>
              <input id="bTel" v-model.trim="form.binome.telephone" placeholder="06 00 00 00 00"/>
              <small v-if="form.binome.telephone && !isPhone(form.binome.telephone)" class="error">Téléphone invalide</small>
            </div>
            <div class="field">
              <label for="bDob">Date de naissance du binôme <span class="req">*</span></label>
              <input id="bDob" v-model="form.binome.dateNaissance" type="date"/>
            </div>
            <div class="field full">
              <small class="hint">L’option binôme ajoute automatiquement 2 parts (soit 20€) au minimum requis.</small>
            </div>
          </div>
        </div>
        <div class="option">
          <label class="checkbox">
            <input type="checkbox" v-model="form.parts.soutien.checked"/>
            <span class="title">Soutenir SuperQuinQuin</span>
          </label>
          <p class="details">
            Je veux soutenir SuperQuinquin en souscrivant a des parts supplémentaires
          </p>
          <div class="subgrid" v-if="form.parts.soutien.checked">
            <div class="field">
              <label for="soutienParts">Nombre de parts supplémentaires</label>
              <div class="stepper">
                <button type="button" class="stepper-btn" @click="decSoutien()">−</button>
                <input id="soutienParts" type="number" min="0" step="1" v-model.number="form.parts.soutien.parts"/>
                <button type="button" class="stepper-btn" @click="incSoutien()">+</button>
              </div>
              <small class="hint">Soit {{ (form.parts.soutien.parts || 0) * 10 }} € supplémentaires</small>
            </div>
          </div>
        </div>
        <div class="subgrid recap">
          <div class="field">
            <label for="nbParts">Nombre total de parts à 10€</label>
            <input id="nbParts" type="number" :value="totalParts" readonly/>
          </div>
          <div class="field">
            <label for="mtTotal">Montant total en euros</label>
            <input id="mtTotal" :value="totalAmount.toFixed(2) + ' €'" readonly/>
          </div>
        </div>
      </section>

      <section class="section">
                <span>En devenant membre, je m’engage à participer au fonctionnement de la coopérative (coopérateurs actifs) à hauteur de 2h45 toutes les 4 semaines.</span>
      </section>

      <section class="section">
        <label class="checkbox accept">
          <input type="checkbox" v-model="form.accepteStatuts"/>
          <span>
            Je déclare avoir pris connaissance et accepter les Statuts provisoires de la coopérative SuperQuinquin sur Deûle. Je déclare avoir bien compris que mes parts sociales sont un investissement soumis à un risque de perte en capital en cas de déficit de la coopérative. Je déclare ne violer aucune norme ou réglementation en souscrivant les parts sociales de la Coopérative.
          </span>
        </label>
      </section>

      <footer class="footer">
        <div class="total">
          <span>Total à payer</span>
          <strong>{{ totalAmount.toFixed(2) }} €</strong>
        </div>
        <button type="submit" class="btn" :disabled="!isFormValid">Payer {{ totalAmount.toFixed(2) }} €</button>
      </footer>
    </form>
  </main>
</template>

<script setup lang="ts">
import {computed, reactive} from 'vue'
import {getSqqInscriptionAPI} from "./api/service/catalog.ts";
import type {Genre} from "./api/model";

const sqqInscriptionAPI = getSqqInscriptionAPI();

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

async function submit() {
  if (!isFormValid.value) return

  try {
    const response = await sqqInscriptionAPI.postApiV1Registrations({
      genre: "MADAME",
      nom: form.nom,
      prenom: form.prenom,
      adresse: form.adresse,
      email: form.email,
      codePostal: form.codePostal,
      etudiantOuMinimasSociaux: false,
      acceptationDesStatus: form.accepteStatuts,
      nombreDePersonnesDansLeFoyer: form.nbFoyer,
      partsDeSoutien: form.parts.soutien.checked ? form.parts.soutien.parts : 0,
      telephone: form.telephone,
      ville: form.ville,
    });

    if (response.headers.location) {
      window.location.href = response.headers.location;
    }
  } catch (error) {
    alert("Une erreur est survenue lors de l'inscription. Veuillez réessayer.");
    console.error(error);
  }
}

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

<style scoped>
:root {
  --text: #1f2937;
  --muted: #6b7280;
  --error: #b3261e;
  --bg: #f6f7fb;
  --surface: #ffffff;
  --border: #e6e8ee;
  --shadow: 0 10px 30px rgba(16, 24, 40, 0.06);
  --accent: #f1dc43;
  --accent-dark: #d8c237;
  --accent-contrast: #111827; /* texte sombre sur jaune */
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

.header::after {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.header h1 {
  margin: 0 0 .35rem;
  font-size: 1.9rem;
}

.brand {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logo {
  display: block;
  height: 96px; /* bigger logo */
}

/* Ensure title aligns well next to the taller logo */
.brand h1 {
  margin: 0 0 .35rem;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.subtitle {
  color: var(--muted);
  margin: 0;
}

.form {
  background: var(--surface);
  border: 1px solid var(--border);
  padding: 1.25rem;
  box-shadow: var(--shadow);
}

.section + .section {
  margin-top: 1.25rem;
}

.section h2 {
  font-size: 1.05rem;
  margin: 0 0 .9rem;
  color: var(--text);
  display: inline-flex;
  align-items: center;
  gap: .5rem;
}

.section h2::after {
  content: '';
  display: inline-block;
  width: 44px;
  height: 6px;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--accent), #ffe66b);
}

.hint {
  color: var(--muted);
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1.15rem 1rem;
}

.subgrid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: .85rem;
}

.full {
  grid-column: 1 / -1;
}

.field {
  display: flex;
  flex-direction: column;
}

.field label {
  font-weight: 600;
  margin-bottom: .35rem;
  color: #111827;
}

.field input {
  background: #fcfcfe;
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: .75rem .9rem;
  font-size: 1rem;
  transition: box-shadow .2s ease, border-color .2s ease, background .2s ease;
}

.field input:hover {
  background: #ffffff;
}

.field input:focus {
  outline: none;
  border-color: var(--accent);
  box-shadow: 0 0 0 4px rgba(241, 220, 67, .35);
}

#cp {
  max-width: 180px;
}

#foyer {
  width: 120px;
  text-align: center;
  appearance: textfield;
}

.inline {
  display: flex;
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
}

.radio, .checkbox {
  display: inline-flex;
  gap: .6rem;
  align-items: center;
  cursor: pointer;
}

input[type="radio"], input[type="checkbox"] {
  accent-color: var(--accent);
}

.title {
  font-weight: 700;
}

.details {
  margin: .25rem 0 .6rem;
  color: var(--muted);
  line-height: 1.45;
}

.req {
  color: #8a7a00;
}

.error {
  color: var(--error);
  font-weight: 600;
}

.option {
  padding: .9rem;
  border: 1px solid var(--border);
  margin-bottom: .85rem;
  background: linear-gradient(180deg, #ffffff, #fdfdfd);
  position: relative;
  box-shadow: 0 4px 18px rgba(16, 24, 40, 0.04);
}

.option::before {
  content: '';
  position: absolute;
  left: -1px;
  top: -1px;
  bottom: -1px;
  width: 5px;
  background: linear-gradient(180deg, var(--accent), #ffe873);
}

.recap .field input {
  background: #fbfbfe;
}

.accept {
  align-items: flex-start;
  gap: .75rem;
}

.accept input {
  margin-top: .35rem;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  margin-top: 1.25rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border);
}

.total {
  display: flex;
  flex-direction: column;
  gap: .125rem;
}

.total span {
  color: var(--muted);
  font-weight: 600;
  font-size: .9rem;
}

.total strong {
  font-size: 1.35rem;
  color: var(--text);
}

.btn {
  background: var(--accent);
  color: var(--accent-contrast);
  border: none;
  border-radius: 12px;
  padding: .9rem 1.35rem;
  font-size: 1.05rem;
  font-weight: 800;
  box-shadow: 0 8px 24px rgba(241, 220, 67, .35);
  transition: transform .12s ease, box-shadow .2s ease, background .2s ease;
}

.btn:disabled {
  opacity: .6;
  cursor: not-allowed;
  box-shadow: none;
}

.btn:not(:disabled):hover {
  background: var(--accent-dark);
  transform: translateY(-1px);
  box-shadow: 0 10px 28px rgba(241, 220, 67, .45);
}

.btn:not(:disabled):active {
  transform: translateY(0);
}

/* Soutien stepper styles */
.stepper {
  display: inline-flex;
  align-items: center;
  gap: .5rem;
}

.stepper-btn {
  width: 38px;
  height: 38px;
  min-width: 38px;
  min-height: 38px;
  border-radius: 999px; /* round */
  border: none;
  background: var(--accent); /* yellow accent */
  color: var(--accent-contrast);
  font-weight: 800;
  font-size: 1.1rem;
  line-height: 1;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 6px 18px rgba(241, 220, 67, .35);
  transition: transform .12s ease, box-shadow .2s ease, background .2s ease;
}

.stepper-btn:hover {
  background: var(--accent-dark);
  transform: translateY(-1px);
  box-shadow: 0 8px 22px rgba(241, 220, 67, .45);
}

.stepper-btn:active {
  transform: translateY(0);
}

#soutienParts {
  width: 120px;
  text-align: center;
  appearance: textfield;
}

#nbParts {
  appearance: textfield;
}

#soutienParts::-webkit-outer-spin-button,
#soutienParts::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

#foyer::-webkit-outer-spin-button,
#foyer::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

#nbParts::-webkit-outer-spin-button,
#nbParts::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

@media (max-width: 720px) {
  .brand {
    flex-direction: column;
    align-items: flex-start;
    gap: .5rem;
  }

  .grid, .subgrid {
    grid-template-columns: 1fr;
  }

  .footer {
    flex-direction: column;
    align-items: stretch;
  }

  .total {
    order: 2;
    align-items: center;
  }

  .btn {
    width: 100%;
    order: 1;
  }
}

:host, :global(body) {
  background: radial-gradient(800px 200px at -10% -20%, rgba(241, 220, 67, 0.18), transparent),
  radial-gradient(800px 200px at 110% -30%, rgba(241, 220, 67, 0.12), transparent),
  var(--bg);
}
</style>
