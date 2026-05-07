package com.ksp.petcaretracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object PetList : Screen("pets")
    data object AddEditPet : Screen("add_edit_pet?petId={petId}") {
        fun createRoute(petId: Long? = null) =
            if (petId != null) "add_edit_pet?petId=$petId" else "add_edit_pet"
    }
    data object PetDetail : Screen("pet_detail/{petId}") {
        fun createRoute(petId: Long) = "pet_detail/$petId"
    }
    data object VaccinationList : Screen("vaccinations/{petId}") {
        fun createRoute(petId: Long) = "vaccinations/$petId"
    }
    data object AddEditVaccination : Screen("add_edit_vaccination/{petId}?vaccinationId={vaccinationId}") {
        fun createRoute(petId: Long, vaccinationId: Long? = null) =
            if (vaccinationId != null) "add_edit_vaccination/$petId?vaccinationId=$vaccinationId"
            else "add_edit_vaccination/$petId"
    }
    data object VetVisitList : Screen("vet_visits/{petId}") {
        fun createRoute(petId: Long) = "vet_visits/$petId"
    }
    data object AddEditVetVisit : Screen("add_edit_vet_visit/{petId}?vetVisitId={vetVisitId}") {
        fun createRoute(petId: Long, vetVisitId: Long? = null) =
            if (vetVisitId != null) "add_edit_vet_visit/$petId?vetVisitId=$vetVisitId"
            else "add_edit_vet_visit/$petId"
    }
    data object DietScheduleList : Screen("diet_schedules/{petId}") {
        fun createRoute(petId: Long) = "diet_schedules/$petId"
    }
    data object AddEditDietSchedule : Screen("add_edit_diet/{petId}?dietId={dietId}") {
        fun createRoute(petId: Long, dietId: Long? = null) =
            if (dietId != null) "add_edit_diet/$petId?dietId=$dietId"
            else "add_edit_diet/$petId"
    }
    data object GrowthLog : Screen("growth_log/{petId}") {
        fun createRoute(petId: Long) = "growth_log/$petId"
    }
    data object AddGrowthLog : Screen("add_growth_log/{petId}") {
        fun createRoute(petId: Long) = "add_growth_log/$petId"
    }
    data object Memories : Screen("memories/{petId}") {
        fun createRoute(petId: Long) = "memories/$petId"
    }
    data object Settings : Screen("settings")
    data object Subscription : Screen("subscription")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.PetList, "Pets", Icons.Filled.Pets, Icons.Outlined.Pets),
    BottomNavItem(Screen.Settings, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
)
