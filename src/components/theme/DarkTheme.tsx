import {createTheme, Theme} from "@mui/material/styles"
import {green} from "@mui/material/colors"

declare module "@mui/material/styles" {
  interface DefaultTheme extends Theme {
  }

  interface Palette {
    neutral: Palette['primary'];
  }

  interface PaletteOptions {
    neutral: PaletteOptions['primary'];
  }
}

export const darkTheme = createTheme({
  palette: {
    mode: "dark",
    background: {
      default: "#111016",
      paper: "#111016",
    },
    primary: {
      main: "#3498db",
    },
    neutral: {
      main: "red"
    },
    secondary: {
      // TODO change
      main: green[500],
    },
    text: {
      primary: "#FFFFFF",
      secondary: "#96a2b4",
    },
  },
  typography: {
    fontFamily: `"Poppins", sans-serif`,
  },
})
