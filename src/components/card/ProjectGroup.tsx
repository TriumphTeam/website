import ProjectCard from "./ProjectCard"
import {Box, Grid} from "@mui/material"
import React from "react"

const ProjectGroup: React.FC<{ tempArray: number[] }> = (prop) => {

  if (prop.tempArray.length === 0) return <Box sx={{
    width: "50%",
    height: "300px",
    borderRadius: "15px",
    background: `url("https://media1.giphy.com/media/hEc4k5pN17GZq/giphy.gif") no-repeat center`,
    backgroundSize: "100%",
  }}/>

  return (
      <>
        {
          prop.tempArray.map(entry => {
            return (
                <Grid item xs={12} md={4}>
                  <ProjectCard/>
                </Grid>
            )
          })
        }
      </>
  )
}

export default ProjectGroup
