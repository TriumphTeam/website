import React from "react"
import {Box, Paper, Typography} from "@mui/material"
import Button from "@mui/material/Button"
import {Project} from "../utils/Utilities"

const ProjectCard: React.FC<{ type: string, project: Project }> = (prop) => {

  const project = prop.project

  return (
      <Paper sx={projectCardSx} elevation={5}>
        <Box sx={{padding: "15px"}}>
          <img src={project.icon}
               alt="project-logo"/>
          <Typography variant="h5">{project.name}</Typography>
          <CardVersion version={project.version} color={project.color}/>
          <Button variant="contained" color="secondary" href={`${prop.type}/${project.id}`}>View</Button>
        </Box>
      </Paper>
  )

}

const CardVersion: React.FC<{ version: string, color: string[] }> = (prop) => {
  return (
      <div className="card-version">
        <Typography
            variant="subtitle1"
            sx={{
              background: `-webkit-linear-gradient(0deg, ${prop.color.join(", ")})`,
              margin: "auto",
              borderRadius: "5px",
              padding: "0 15px",
              display: "inline-block",
            }}
        >{prop.version}</Typography>
      </div>
  )
}

const projectCardSx = {
  width: "250px",
  margin: "auto",
  borderRadius: "15px",
  background: "#141416",
  transition: "ease 0.3s",
  "& img": {
    width: "50%",
    padding: "10px 0",
  },
  "&:hover": {
    transform: `scale(1.05)`,
  },
}

export default ProjectCard
