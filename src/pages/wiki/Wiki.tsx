import React from "react"
import "./wiki.scss"
import SideBar from "../../components/wiki/SideBar"
import {Redirect, useParams} from "react-router-dom"
import WikiBody from "../../components/wiki/WikiContent"
import {Box, Grid} from "@mui/material"
import TableOfContents from "../../components/wiki/TableOfContents"
import {SideBarSize} from "../../components/axios/Types"

export default function Wiki() {
  // Url data
  const {type, project} = useParams<{ type?: string, project?: string, optionalPage?: string }>()

  const url = `/${type}/${project}`

  // Guarantees it's a valid type
  if (type !== "library" && type !== "plugin") return <Redirect to="/404"/>

  return (
      <Box sx={{
        "& .active": {
          color: (theme) => `${theme.palette.primary.main} !important`,
          fontWeight: "bold",
        },
      }}>
        <SideBar url={url}/>
        <Box sx={{
          padding: "15px",
          marginLeft: SideBarSize,
        }}>
          <Grid container spacing={4}>
            <Grid item xs={10}>
              <WikiBody url={url}/>
            </Grid>
            <Grid item xs={2}>
              <TableOfContents url={url}/>
            </Grid>
          </Grid>
        </Box>
      </Box>
  )
}
