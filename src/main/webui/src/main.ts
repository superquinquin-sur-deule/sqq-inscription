import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import './style.css'
import App from './App.vue'
import Registration from "./views/Registration.vue";
import AdminCooperateurs from "./views/AdminCooperateurs.vue";
import PartsSupplementaires from "./views/PartsSupplementaires.vue";
import PaymentResult from "./views/PaymentResult.vue";


const routes = [
    { path: '/', component: Registration },
    { path: '/payment-result', component: PaymentResult },
    { path: '/admin', component: AdminCooperateurs },
    { path: '/parts-supplementaires', component: PartsSupplementaires },
]

const router = createRouter({
    history: createWebHistory('/'),
    routes
})


const app = createApp(App)
app.use(router)
app.mount('#app')