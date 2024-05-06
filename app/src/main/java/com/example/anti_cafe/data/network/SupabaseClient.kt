package com.example.anti_cafe.data.network


import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = com.example.anti_cafe.BuildConfig.supabaseUrl,
        supabaseKey = com.example.anti_cafe.BuildConfig.supabaseKey
    ){
        install(Postgrest)
        install(Auth)
    }
}