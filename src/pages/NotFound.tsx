import React from "react"
import {Container, Typography} from "@mui/material"
import Footer from "../components/footer/Footer"
import {Link} from "react-router-dom"

export default function NotFound() {
  return <>
    <Container sx={{
      height: "100vh",
      display: "flex",
      justifyItems: "center",
      justifyContent: "center",
      alignItems: "center",
    }}>
      <Typography variant="h1" sx={{
        fontWeight: "bold",
        "& a": {
          color: "inherit",
        },
      }}>
        <Link to="/">404</Link>
      </Typography>
    </Container>
    <Footer fixed/>
  </>
}
