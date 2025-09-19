/** @type {import('tailwindcss').Config} */
export default {
  content: ['./src/**/*.{astro,html,js,jsx,md,mdx,svelte,ts,tsx,vue}'],
  theme: {
    extend: {
      colors: {
        // Tema Claro (Público)
        primary: {
          50: '#FFFFFF',
          100: '#F9FAFB',
          900: '#111827',
        },
        // Tema Oscuro (Privado)
        dark: {
          50: '#EAEAEA',
          900: '#1A1A2E',
        },
        accent: {
          500: '#7F56D9', // Morado/azul eléctrico
        },
      },
      fontFamily: {
        sans: ['Inter', 'Manrope', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
