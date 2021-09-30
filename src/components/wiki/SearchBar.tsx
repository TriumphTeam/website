import React from "react"
import {alpha, Autocomplete, TextField} from "@mui/material"

const SearchBar = () => {
  return <Autocomplete
      sx={{padding: "10px 25px"}}
      freeSolo
      id="free-solo-2-demo"
      disableClearable
      options={["search1", "search2"]}
      renderInput={(params) => (
          <TextField
              sx={{
                background: (theme) => alpha(theme.palette.common.white, 0.025),
                borderRadius: "10px",
                transition: "ease 0.3s",
                "&:hover": {
                  background: (theme) => alpha(theme.palette.common.white, 0.05),
                },
              }}
              {...params}
              label="Search.."
              InputProps={{
                ...params.InputProps,
                className: "search-input",
                type: "search",
              }}
          />
      )}
  />
}

export default SearchBar
