import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import './style.css'
import App from './App.vue'
import Registration from "./views/Registration.vue";
import SuccessfulRegistration from "./views/SuccessfulRegistration.vue";
import AdminCooperateurs from "./views/AdminCooperateurs.vue";


const routes = [
    { path: '/', component: Registration },
    { path: '/success', component: SuccessfulRegistration },
    { path: '/admin', component: AdminCooperateurs },
]

const router = createRouter({
    history: createWebHistory('/'),
    routes
})


const app = createApp(App)
app.use(router)
app.mount('#app')