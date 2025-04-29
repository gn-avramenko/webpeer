import { UserConfig, ConfigEnv } from 'vite';
import { resolve } from 'path';

// https://vitejs.dev/config/
const defineConfig = ({ mode, command }: ConfigEnv): UserConfig => {
  // @ts-ignore
  const config = {
    server: {
      open: true,
      proxy: {
        '/_ui': {
          target: 'http://localhost:8080/',
          changeOrigin: false,
        },
        '/_resources': {
          target: 'http://localhost:8080/',
          changeOrigin: false,
        },
      },
    },
    build: {
      rollupOptions: {
        output: {
          entryFileNames: 'assets/[name].[hash].js',
          chunkFileNames: 'assets/[name].[hash].js',
          assetFileNames: 'assets/[name].[hash][extname]',
          manualChunks: (id:string) => {
            if (true) {
              return 'vendor';
            }
            return 'vendor';
          },
        },
        input: {
          app: './index.html', // default
        },
      },
    },
    resolve: {
      alias: {
        '@': resolve(__dirname, './src'), // Базовый алиас для папки src
      },
    },
  };
  return config;
};
export default defineConfig;
