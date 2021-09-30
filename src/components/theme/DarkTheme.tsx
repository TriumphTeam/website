import {createTheme, Theme} from "@mui/material/styles"

declare module "@mui/material/styles" {
  interface DefaultTheme extends Theme {
  }
}

export const darkTheme = createTheme({
  palette: {
    mode: "dark",
    background: {
      default: "#141417",
      paper: "#141417",
    },
    primary: {
      main: "#3498db",
    },
    secondary: {
      main: "#1D1D1F",
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
