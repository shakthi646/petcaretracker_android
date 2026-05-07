package com.ksp.petcaretracker.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ksp.petcaretracker.ui.screens.diet.AddEditDietScreen
import com.ksp.petcaretracker.ui.screens.diet.DietScheduleListScreen
import com.ksp.petcaretracker.ui.screens.growth.AddGrowthLogScreen
import com.ksp.petcaretracker.ui.screens.growth.GrowthLogScreen
import com.ksp.petcaretracker.ui.screens.home.HomeScreen
import com.ksp.petcaretracker.ui.screens.memories.MemoriesScreen
import com.ksp.petcaretracker.ui.screens.pet.AddEditPetScreen
import com.ksp.petcaretracker.ui.screens.pet.PetDetailScreen
import com.ksp.petcaretracker.ui.screens.pet.PetListScreen
import com.ksp.petcaretracker.ui.screens.settings.SettingsScreen
import com.ksp.petcaretracker.ui.screens.vaccination.AddEditVaccinationScreen
import com.ksp.petcaretracker.ui.screens.vaccination.VaccinationListScreen
import com.ksp.corelibrary.module.subscription.screen.SubscriptionScreen
import com.ksp.petcaretracker.ui.screens.vet.AddEditVetVisitScreen
import com.ksp.petcaretracker.ui.screens.vet.VetVisitListScreen

private const val ANIM_DURATION = 350

@Composable
fun PetCareNavGraph(navController: NavHostController, bottomPadding: PaddingValues = PaddingValues()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier
            .padding(bottom = bottomPadding.calculateBottomPadding())
            .consumeWindowInsets(PaddingValues(bottom = bottomPadding.calculateBottomPadding())),
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIM_DURATION)) + fadeIn(tween(ANIM_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIM_DURATION)) + fadeOut(tween(ANIM_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(ANIM_DURATION)) + fadeIn(tween(ANIM_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(ANIM_DURATION)) + fadeOut(tween(ANIM_DURATION))
        }
    ) {
        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToPetDetail = { navController.navigate(Screen.PetDetail.createRoute(it)) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAddPet = { navController.navigate(Screen.AddEditPet.createRoute()) },
                onNavigateToSubscription = { navController.navigate(Screen.Subscription.route) }
            )
        }

        // Pet List
        composable(Screen.PetList.route) {
            PetListScreen(
                onNavigateToAddPet = { navController.navigate(Screen.AddEditPet.createRoute()) },
                onNavigateToPetDetail = { navController.navigate(Screen.PetDetail.createRoute(it)) },
                onNavigateToSubscription = { navController.navigate(Screen.Subscription.route) }
            )
        }

        // Add/Edit Pet
        composable(
            route = Screen.AddEditPet.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType; nullable = true; defaultValue = null })
        ) {
            AddEditPetScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Pet Detail
        composable(
            route = Screen.PetDetail.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) {
            PetDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditPet = { navController.navigate(Screen.AddEditPet.createRoute(it)) },
                onNavigateToVaccinations = { navController.navigate(Screen.VaccinationList.createRoute(it)) },
                onNavigateToVetVisits = { navController.navigate(Screen.VetVisitList.createRoute(it)) },
                onNavigateToDiet = { navController.navigate(Screen.DietScheduleList.createRoute(it)) },
                onNavigateToGrowthLog = { navController.navigate(Screen.GrowthLog.createRoute(it)) },
                onNavigateToMemories = { navController.navigate(Screen.Memories.createRoute(it)) }
            )
        }

        // Vaccination List
        composable(
            route = Screen.VaccinationList.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) {
            VaccinationListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { petId -> navController.navigate(Screen.AddEditVaccination.createRoute(petId)) },
                onNavigateToEdit = { petId, vacId -> navController.navigate(Screen.AddEditVaccination.createRoute(petId, vacId)) }
            )
        }

        // Add/Edit Vaccination
        composable(
            route = Screen.AddEditVaccination.route,
            arguments = listOf(
                navArgument("petId") { type = NavType.StringType },
                navArgument("vaccinationId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) {
            AddEditVaccinationScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Vet Visit List
        composable(
            route = Screen.VetVisitList.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) {
            VetVisitListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { petId -> navController.navigate(Screen.AddEditVetVisit.createRoute(petId)) },
                onNavigateToEdit = { petId, visitId -> navController.navigate(Screen.AddEditVetVisit.createRoute(petId, visitId)) }
            )
        }

        // Add/Edit Vet Visit
        composable(
            route = Screen.AddEditVetVisit.route,
            arguments = listOf(
                navArgument("petId") { type = NavType.StringType },
                navArgument("vetVisitId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) {
            AddEditVetVisitScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Diet Schedule List
        composable(
            route = Screen.DietScheduleList.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) {
            DietScheduleListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { petId -> navController.navigate(Screen.AddEditDietSchedule.createRoute(petId)) },
                onNavigateToEdit = { petId, dietId -> navController.navigate(Screen.AddEditDietSchedule.createRoute(petId, dietId)) }
            )
        }

        // Add/Edit Diet Schedule
        composable(
            route = Screen.AddEditDietSchedule.route,
            arguments = listOf(
                navArgument("petId") { type = NavType.StringType },
                navArgument("dietId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) {
            AddEditDietScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Growth Log
        composable(
            route = Screen.GrowthLog.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) {
            GrowthLogScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddLog = { petId -> navController.navigate(Screen.AddGrowthLog.createRoute(petId)) }
            )
        }

        // Add Growth Log
        composable(
            route = Screen.AddGrowthLog.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) {
            AddGrowthLogScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Memories
        composable(
            route = Screen.Memories.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) {
            MemoriesScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSubscription = { navController.navigate(Screen.Subscription.route) }
            )
        }

        // Subscription
        composable(Screen.Subscription.route) {
            SubscriptionScreen(navController = navController)
        }
    }
}
