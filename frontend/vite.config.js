import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  optimizeDeps: {
    exclude: ['lucide-react'], // No problem here
  },
  build: {
    rollupOptions: {
      // Optional, Vite auto-detects entry point, so you could remove this line
      input: 'src/main.jsx'
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'https://rdm-backend-raju-a-dev.apps.rm3.7wse.p1.openshiftapps.com',
        changeOrigin: true,
        secure: false, // Allow self-signed certs
      }
    }
  }
});
