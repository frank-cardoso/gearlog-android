package br.edu.unisatc.gearlog.data.remote

import br.edu.unisatc.gearlog.model.FipeOption

fun FipeOptionDto.toDomain(): FipeOption = FipeOption(
    code = code,
    name = name
)

