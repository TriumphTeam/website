import React, {useEffect, useState} from "react"
import {styled, Theme} from "@mui/material/styles"
import {Box, Button, Container, IconButton, Typography} from "@mui/material"
import {SxProps} from "@mui/system/"
import "./home.scss"
import {HashLink} from "react-router-hash-link"
import Particles from "react-particles-js"

function Home() {
  const [scrollOffSet, setScrollOffSet] = useState(0)
  const handleScroll = () => setScrollOffSet(window.scrollY)

  useEffect(() => {
    window.addEventListener("scroll", handleScroll)

    return () => window.removeEventListener("scroll", handleScroll)
  }, [])

  const openLink = (link: string) => {
    window.open(link)
  }

  return (
      <>
        <div className="parent">
          <div className="bg-circles" style={{transform: `translateY(${scrollOffSet * 0.2}px)`}}>
            <Particle/>
          </div>
          <div className="bg-hollow-circles" style={{transform: `translateY(${scrollOffSet * 0.5}px)`}}/>
          <Container>
            <Box sx={{height: "100vh"}} className="center">
              <div className="logo-container">
                <div className="main-logo"/>
                <Typography variant="h2" sx={{fontFamily: "Poppins", fontWeight: "bold"}}>Triumph Team</Typography>
                <Typography variant="h3" sx={{fontFamily: "Poppins", fontWeight: "200", marginTop: "25px"}}>
                  A development team with a passion for <HashLink to="#plugins"
                                                                  className="gradient-text">plugins</HashLink>, <HashLink
                    to="#libraries" className="gradient-text">libraries</HashLink>, and much <HashLink to="#more"
                                                                                                       className="gradient-text">more</HashLink>.
                </Typography>
                <HomeButton onClick={() => openLink("https://mattstudios.me/discord")}><i
                    className="fab fa-discord"/></HomeButton>
                <HomeButton onClick={() => openLink("https://github.com/TriumphTeam")}><i
                    className="fab fa-github"/></HomeButton>
              </div>
              <HashLink to="#test" className="scroll-link">
                <div className="scroll-down">
                  <span className="text">Projects</span>
                  <div className="chevron"/>
                  <div className="chevron"/>
                  <div className="chevron"/>
                </div>
              </HashLink>
            </Box>
          </Container>
        </div>
        <Box sx={{height: "100vh", width: "100%"}} className="center">
          <Typography variant="h3" id="plugins">Plugins</Typography>
        </Box>
      </>

  )
}

const HomeButton = styled(IconButton)({
  width: "75px",
  background: "#1D1D1F",
  borderRadius: "15px",
  padding: "15px",
  fontSize: "2.5em",
  margin: "50px 15px",
  transition: "ease 0.25s",
  "&:hover": {
    transform: "scale(1.1)",
  },
})

const Particle = () => <Particles
    params={{
      particles: {
        color: {
          value: "#26c885",
        },
        links: {
          enable: false,
        },
        size: {
          value: 50,
          animation: {
            enable: false,
          },
          random: {
            enable: true,
            minimumValue: 5,
          },
        },
        opacity: {
          value: 0.05,
          random: {
            enable: true,
            minimumValue: 0.005,
          },
          animation: {
            enable: false,
          },
        },
        move: {
          speed: 0.1,
        },
        number: {
          density: {
            enable: false,
          },
          value: 15,
        },
      },

    }}
/>

export default Home
