import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import './style.css'
import App from './App.vue'
import Registration from "./views/Registration.vue";
import SuccessfulRegistration from "./views/SuccessfulRegistration.vue";
import AdminCooperateurs from "./views/AdminCooperateurs.vue";
import PartsSupplementaires from "./views/PartsSupplementaires.vue";
import SuccessfulPartsSupplementaires from "./views/SuccessfulPartsSupplementaires.vue";


const routes = [
    { path: '/', component: Registration },
    { path: '/success', component: SuccessfulRegistration },
    { path: '/admin', component: AdminCooperateurs },
    { path: '/parts-supplementaires', component: PartsSupplementaires },
    { path: '/parts-supplementaires-success', component: SuccessfulPartsSupplementaires },
]

const router = createRouter({
    history: createWebHistory('/'),
    routes
})


const app = createApp(App)
app.use(router)
app.mount('#app')