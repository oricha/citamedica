/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
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
