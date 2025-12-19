<template>
  <main class="container">
    <header class="header">
      <div class="brand">
        <img src="/superquinquin_logo_deule.svg" alt="SuperQuinquin sur Deûle" class="logo" />
        <div class="brand-text">
          <h1>Parts Supplémentaires</h1>
          <p class="subtitle">Tu es déjà sociétaire et tu souhaites souscrire des parts supplémentaires.</p>
        </div>
      </div>
    </header>

    <form class="form" method="post" action="/api/v1/parts-supplementaires">
      <section class="section">
        <h2>Tes informations</h2>
        <div class="grid">
          <div class="field">
            <label for="prenom">Prénom <span class="req">*</span></label>
            <input id="prenom" name="prenom" v-model.trim="form.prenom" required/>
          </div>

          <div class="field">
            <label for="nom">Nom <span class="req">*</span></label>
            <input id="nom" name="nom" v-model.trim="form.nom" required/>
          </div>

          <div class="field full">
            <label for="email">Email <span class="req">*</span></label>
            <input id="email" name="email" v-model.trim="form.email" type="email" placeholder="toi@exemple.fr"/>
            <small v-if="form.email && !isEmail(form.email)" class="error">Email invalide</small>
          </div>
        </div>
      </section>

      <section class="section">
        <h2>Parts supplémentaires <span class="req">*</span></h2>
        <p class="hint">Choisis le nombre de parts supplémentaires que tu souhaites souscrire.</p>

        <div class="option">
          <div class="field">
            <label for="partsSupplementaires">Nombre de parts supplémentaires (10€ chacune)</label>
            <div class="stepper">
              <button type="button" class="stepper-btn" @click="decParts()">−</button>
              <input id="partsSupplementaires" name="partsSupplementaires" v-model.number="form.partsSupplementaires" type="number" min="1" step="1"/>
              <button type="button" class="stepper-btn" @click="incParts()">+</button>
            </div>
          </div>
        </div>

        <div class="subgrid recap">
          <div class="field">
            <label for="nbParts">Nombre total de parts</label>
            <input id="nbParts" type="number" :value="form.partsSupplementaires" readonly/>
          </div>
          <div class="field">
            <label for="mtTotal">Montant total en euros</label>
            <input id="mtTotal" :value="totalAmount.toFixed(2) + ' €'" readonly/>
          </div>
        </div>
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
  --accent-contrast: #111827;
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
  height: 96px;
}

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
  border-radius: 999px;
  border: none;
  background: var(--accent);
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

#partsSupplementaires {
  width: 120px;
  text-align: center;
  appearance: textfield;
}

#nbParts {
  appearance: textfield;
}

#partsSupplementaires::-webkit-outer-spin-button,
#partsSupplementaires::-webkit-inner-spin-button {
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
