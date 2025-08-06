<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <div class="content-wrapper">
          <router-view />
        </div>
      </a-layout-content>
      <a-layout-footer class="footer">
        <div class="footer-content">
          <p>&copy; 2024 Br OJ - Online Programming Evaluation System</p>
          <p>Built with ❤️ for the programming community</p>
        </div>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#basicLayout {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

#basicLayout .header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
  transition: all 0.3s ease;
}

#basicLayout .content {
  background: transparent;
  padding: 24px;
  min-height: calc(100vh - 120px);
}

#basicLayout .content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 32px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

#basicLayout .footer {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding: 20px;
  text-align: center;
  position: relative;
  z-index: 100;
}

#basicLayout .footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

#basicLayout .footer-content p {
  margin: 4px 0;
  color: #666;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  #basicLayout .content {
    padding: 16px;
  }

  #basicLayout .content-wrapper {
    padding: 20px;
    margin: 0 8px;
  }

  #basicLayout .footer {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  #basicLayout .content {
    padding: 12px;
  }

  #basicLayout .content-wrapper {
    padding: 16px;
    margin: 0 4px;
  }
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}
</style>

<script>
import GlobalHeader from "@/components/GlobalHeader";

export default {
  components: { GlobalHeader },
};
</script>
