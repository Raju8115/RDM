import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  optimizeDeps: {
    exclude: ['lucide-react'],
  },
  build: {
    rollupOptions: {
      input: '/src/main.jsx', // Explicitly set the entry point
    },
  },
  server: {
    proxy: {
      '/api': {
      target: 'https://rdm-backend-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com', // <-- use your real backend URL
        changeOrigin: true,
        secure: false,
      }
    }
  }
});
