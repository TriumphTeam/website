import React, {useEffect, useState} from "react"
import "./home.scss"
import {Box, Container, Grid, Typography} from "@mui/material"
import {HashLink} from "react-router-hash-link"
import CircleParticle from "../../components/particles/CircleParticle"
import BigIconButton from "../../components/buttons/BigIconButton"
import {openLink, Projects} from "../../components/utils/Utilities"
import ProjectGroup from "../../components/card/ProjectGroup"
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome"
import {faDiscord, faGithub} from "@fortawesome/free-brands-svg-icons"
import useSWR from "swr"
import Footer from "../../components/footer/Footer"

const Home = () => {
  const [scrollOffSet, setScrollOffSet] = useState(0)
  const handleScroll = () => setScrollOffSet(window.scrollY)

  // API data
  const {data: projects} = useSWR<Projects>(`projects`)

  useEffect(() => {
    window.addEventListener("scroll", handleScroll)

    return () => window.removeEventListener("scroll", handleScroll)
  }, [])

  return (
      <>
        <div className="parent">
          <div className="bg-circles" style={{transform: `translateY(${scrollOffSet * 0.2}px)`}}>
            <CircleParticle/>
          </div>
          <div className="bg-hollow-circles" style={{transform: `translateY(${scrollOffSet * 0.5}px)`}}/>
          <Container>
            <Box sx={{height: "100vh"}} className="center">
              <Grid container className="text-container">
                <Grid item xs={12}>
                  <div className="main-logo"/>
                </Grid>
                <Grid item xs={12}>
                  <Typography variant="h2" sx={{fontWeight: "bold"}}>TRIUMPH<span className="test"> </span>TEAM</Typography>
                  <Typography variant="h3" sx={{fontWeight: "100", marginTop: "25px"}}>
                    A development team with a passion for <HashLink to="#plugins">plugins</HashLink>, <HashLink
                      to="#libraries">libraries</HashLink>, and much <span className="gradient">more</span>.
                  </Typography>
                </Grid>
                <Grid item xs={12} sx={{
                  "& button": {
                    margin: "50px 15px",
                  },
                }}>
                  <BigIconButton onClick={() => openLink("https://mattstudios.me/discord")}>
                    <FontAwesomeIcon icon={faDiscord}/>
                  </BigIconButton>
                  <BigIconButton onClick={() => openLink("https://github.com/TriumphTeam")}>
                    <FontAwesomeIcon icon={faGithub}/>
                  </BigIconButton>
                </Grid>
              </Grid>
              <HashLink to="#plugins" className="scroll-link">
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
        <Container sx={{textAlign: "center"}}>
          <Grid container justifyContent="center" alignItems="center" spacing={3}>
            <Grid item xs={12}><Typography variant="h3" id="plugins" sx={{padding: "50px"}}>Plugins</Typography></Grid>
            <ProjectGroup type="plugin" projects={projects?.plugin}/>
          </Grid>
          <Grid container justifyContent="center" alignItems="center" spacing={3}>
            <Grid item xs={12}>
              <Typography variant="h3" id="libraries" sx={{padding: "50px"}}>Libraries</Typography>
            </Grid>
            <ProjectGroup type="library" projects={projects?.library}/>
          </Grid>
        </Container>
        <Footer/>
      </>
  )
}

export default Home
