import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  css: {
    preprocessorOptions: {
      scss: {}
    }
  },
  optimizeDeps: {
    exclude: ['lucide-react'],
  },
  server: {
    port: 5174,
    proxy: {
      '/api': {
        target: 'https://rdm-backend-3-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com',
        changeOrigin: true,
        secure: false,
      }
    }
  },
});
