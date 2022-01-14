import ProjectCard from "./ProjectCard"
import {Box, Grid} from "@mui/material"
import React from "react"
import {Project} from "../utils/Utilities"

const ProjectGroup: React.FC<{ type: string, projects: Project[] | undefined }> = (prop) => {

  const projects = prop.projects

  if (!projects || projects.length === 0) return <Box sx={{
    width: "50%",
    height: "300px",
    borderRadius: "15px",
    background: `url("https://media1.giphy.com/media/hEc4k5pN17GZq/giphy.gif") no-repeat center`,
    backgroundSize: "100%",
  }}/>

  return (
      <>
        {
          projects.map(project => {
            return (
                <Grid item xs={12} md={4}>
                  <ProjectCard type={prop.type} project={project}/>
                </Grid>
            )
          })
        }
      </>
  )
}

export default ProjectGroup
