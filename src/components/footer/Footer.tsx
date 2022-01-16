import Paper from "@mui/material/Paper"
import React, {CSSProperties} from "react"
import {Typography} from "@mui/material"

export const Footer: React.FC<{ fixed?: boolean }> = (prop) => {
  const style: CSSProperties = prop.fixed ? fixedSx : {}
  return <div style={style}>
    <Paper sx={{
      background: "#141416",
      height: "50px",
      textAlign: "center",
      display: "flex",
      justifyContent: "center",
      justifyItems: "center",
      alignItems: "center",
      marginTop: "5%",
      "& a": {
        color: "inherit",
      },
    }} elevation={5}>
      <Typography sx={{display: "flex-box"}} variant="subtitle2">
        Copyright © 2022, <a href="https://github.com/TriumphTeam">TriumphTeam</a>. All Rights Reserved.
      </Typography>
    </Paper>
  </div>
}

const fixedSx: CSSProperties = {
  width: "100%",
  position: "fixed",
  bottom: 0,
  zIndex: 10,
}

export default Footer
