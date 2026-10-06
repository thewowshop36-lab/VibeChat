/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        'wa-green': '#00A884',
        'wa-green-dark': '#008069',
        'wa-green-light': '#25D366',
        'wa-teal': '#128C7E',
        'wa-bg': '#EFEAE2',
        'wa-panel': '#202C33',
        'wa-deep': '#111B21',
        'wa-chat': '#0B141A',
        'wa-out': '#D9FDD3',
        'wa-outDark': '#005C4B',
      }
    },
  },
  plugins: [],
}
