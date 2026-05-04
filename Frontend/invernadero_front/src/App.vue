<script setup>
import { RouterView, useRoute } from 'vue-router'
import Sidebar from '../src/components/Sidebar.vue'

const route = useRoute()
</script>

<template>
  <RouterView v-if="route.path === '/login'" />

  <div v-else class="app-layout">
    <Sidebar />

    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  background: #ffffff;
}

.main-content {
  flex: 1;
  min-width: 0;
  min-height: 100vh;
  margin-left: 260px;
  padding: 32px;
  background: #ffffff;
  overflow-y: auto;
}

@media (max-width: 760px) {
  .app-layout {
    flex-direction: column;
  }

  .main-content {
    margin-left: 0;
    min-height: auto;
    padding: 22px 16px;
  }
}
</style>
