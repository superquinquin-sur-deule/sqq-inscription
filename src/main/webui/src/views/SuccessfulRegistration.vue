<template>
  <main class="container">
    <header class="header">
      <div class="brand">
        <img src="/superquinquin_logo_deule.svg" alt="SuperQuinquin sur Deûle" class="logo"/>
        <div class="brand-text">
          <h1>Merci pour votre inscription !</h1>
          <p class="subtitle">Votre paiement a bien été confirmé. Bienvenue à la Coopérative SuperQuinquin sur
            Deûle.</p>
        </div>
      </div>
    </header>

    <section class="section success">
      <div class="success-icon" aria-hidden="true">✓</div>
      <h2>Inscription finalisée</h2>
      <p>
        Nous venons de valider votre souscription de parts sociales. Vous recevrez un email de confirmation
        avec un récapitulatif de votre inscription et de votre paiement.
      </p>

      <div class="next-steps">
        <h3>Et maintenant ?</h3>
        <ul>
          <li>Surveillez votre boîte mail pour le message de confirmation.</li>
          <li>Blablabla</li>
          <li>blablablabla</li>
        </ul>
      </div>

      <div class="actions">
        <router-link to="/" class="btn primary">Retour à l’inscription</router-link>
      </div>

      <p class="help">
        Besoin d’aide ? Écrivez-nous à
        <a href="mailto:contact@superquinquin.fr">contact@superquinquin.fr</a>.
      </p>
    </section>
  </main>
</template>

<script setup lang="ts">
import {onMounted} from "vue";
import {getSqqInscriptionAPI} from "../api/service/catalog.ts";

onMounted(() => {
  let urlParams = new URLSearchParams(window.location.search);
  const sqqInscriptionAPI = getSqqInscriptionAPI();
  const cooperateurId = parseInt(urlParams.get("cooperateurId")!);
  sqqInscriptionAPI.postApiV1RegistrationsSuccessCooperateurId(cooperateurId)
})
</script>

<style scoped>
/* Reuse the same look & feel as the registration page */
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
}

.section + .section {
  margin-top: 1.25rem;
}

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

.actions {
  margin-top: 1rem;
}

.btn {
  display: inline-block;
  padding: .6rem 1rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
}

.btn.primary {
  background: var(--accent);
  color: var(--accent-contrast);
}

.btn.primary:focus,
.btn.primary:hover {
  background: var(--accent-dark);
}

.help {
  margin-top: 1rem;
  color: var(--muted);
}
</style>