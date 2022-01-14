import React from "react"
import {Theme} from "@mui/material/styles"
import {Container} from "@mui/material"
import Drawer from "@mui/material/Drawer"
import {SideBarSize} from "../utils/Utilities"
import {Link, Redirect, useParams} from "react-router-dom"
import useSWR from "swr"
import Logo from "../../imgs/logo.png"
import SearchBar from "./SearchBar"
import {SxProps} from "@mui/system"

/**
 * Entry type for easy mapping the summary API call
 */
type Entry =
    | { type: "HEADER", literal: string, }
    | { type: "ITEM", literal: string, destination: string }
    | { type: "LIST", children: Entry[] }

export const SideBar: React.FC<{ url: string }> = ({url}) => {

  const {type, project, page} = useParams<{ type?: string, project?: string, page?: string }>()

  // API call for the summary data
  const {data: summary, error} = useSWR<Entry[]>(`project/${type}/summary/${project}`)

  // TODO right now this will redirect on any error, might wanna change to only 404 or something
  if (error) return <Redirect to="/404"/>

  // Function to check if the link is currently active
  const isActive = (destination: string) => destination === page

  // Could use some improvement make it more DRY
  const renderItem = (destination: string, literal: string, key: string) => {
    if (isActive(destination)) return <li key={key}><Link className="active" to={destination}>{literal}</Link>
    </li>
    return <li key={key}><Link to={destination}>{literal}</Link></li>
  }

  const renderList = (children: Entry[], key: string) => {
    return (
        <ul key={`ul-${key}`}>
          {
            children.map((entry, index) => {
              if (entry.type === "ITEM") return renderItem(entry.destination, entry.literal, `${key}-${index}`)
              if (entry.type === "LIST") return renderList(entry.children, `${key}-${index}`)
              else return <></>
            })
          }
        </ul>
    )
  }

  return (
      <Drawer
          className="side-bar"
          sx={{
            width: SideBarSize,
            flexShrink: 0,
            "& .MuiDrawer-paper": {
              width: SideBarSize,
              border: "none",
            },
          }}
          variant="permanent"
      >
        <Link id="logo" to=""><img src={Logo} alt="logo"/></Link>
        <SearchBar/>
        <Container sx={containerSx}>
          <div className="side-list">
            {
              summary?.map((entry, index) => {
                if (entry.type === "ITEM") return renderItem(entry.destination, entry.literal, `${entry.destination}-${index}`)
                if (entry.type === "HEADER") return <h1 key={`${entry.literal}-${index}`}>{entry.literal}</h1>
                if (entry.type === "LIST") return renderList(entry.children, `${index}`)
                return <></>
              })
            }
            <div className="bottom-space"/>
          </div>
        </Container>
      </Drawer>
  )
}

const containerSx: SxProps<Theme> = {
  overflow: "hidden",
  position: "sticky",
  padding: "20px 0 !important",
  maxHeight: "calc(100vh - 100px)",
  "&::after": {
    position: "absolute",
    zIndex: 10,
    left: 0,
    width: "100%",
    height: "10%",
    content: `""`,
    bottom: 0,
    background: (theme) => `linear-gradient(180deg, ${theme.palette.background.default}00 0%, ${theme.palette.background.default}FF 50%)`,
  },
  "& a:hover": {
    color: (theme) => `${theme.palette.primary.main} !important`,
  },
}

export default SideBar
