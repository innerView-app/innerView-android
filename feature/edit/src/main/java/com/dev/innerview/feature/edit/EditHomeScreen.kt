package com.dev.innerview.feature.edit

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.innerview.core.designsystem.component.InnerViewDialog
import com.dev.innerview.core.designsystem.component.InnerViewDialogTextField
import com.dev.innerview.core.designsystem.component.InnerViewDropdownMenu
import com.dev.innerview.core.designsystem.component.InnerViewDropdownMenuItem
import com.dev.innerview.core.designsystem.component.InnerViewFloatingActionButton
import com.dev.innerview.core.designsystem.component.InnerViewTopAppBar
import com.dev.innerview.core.designsystem.component.InterviewGroupCard
import com.dev.innerview.core.designsystem.component.OutlinedText
import com.dev.innerview.core.designsystem.component.appBarSize
import com.dev.innerview.core.designsystem.theme.InnerViewTheme
import com.dev.innerview.core.designsystem.theme.Paddings
import com.dev.innerview.core.model.InnerProject
import com.dev.innerview.feature.edit.model.EditHomeUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun EditHomeScreen(
    padding: PaddingValues,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    navigateToEdit: (Int) -> Unit,
    viewModel: EditHomeViewModel = hiltViewModel(),
) {

    val editHomeUiState by viewModel.editHomeUiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.errorFlow.collectLatest { throwable -> onShowErrorSnackBar(throwable) }
    }

    EditHomeContent(
        editHomeUiState = editHomeUiState,
        padding = padding,
        onSelectInnerViewCreate = viewModel::selectInnerProjectCreate,
        maxInnerProjectTitleLength = viewModel.maxInnerProjectTitleLength,
        updateDialogInnerProjectTitle = viewModel::updateDialogInnerProjectTitle,
        addInnerProject = viewModel::addInnerProject,
        deleteInnerProject = viewModel::deleteInnerProject,
        navigateToEdit = navigateToEdit,
    )
}

@Composable
private fun EditHomeContent(
    editHomeUiState: EditHomeUiState,
    padding: PaddingValues,
    onSelectInnerViewCreate: () -> Unit,
    maxInnerProjectTitleLength: Int,
    updateDialogInnerProjectTitle: (String) -> Unit,
    addInnerProject: () -> Unit,
    deleteInnerProject: (Int) -> Unit,
    navigateToEdit: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = stringResource(R.string.feature_edit_home_top_app_bar_title)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .padding(top = appBarSize)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                InnerProjectList(
                    interviewGroups = editHomeUiState.innerProjects,
                    deleteInnerProject = deleteInnerProject,
                    navigateToEdit = navigateToEdit
                )
                InnerViewFloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = Paddings.large, bottom = Paddings.large),
                    iconImageVector = Icons.Filled.Add,
                    text = stringResource(R.string.feature_edit_new_project),
                    onClick = { onSelectInnerViewCreate() }
                )
            }
        }
    }

    if (editHomeUiState.isInnerProjectCreateDialogVisible) {
        InnerViewDialog(
            titleText = stringResource(R.string.feature_edit_create_project),
            contentText = stringResource(R.string.feature_edit_create_project_content_text),
            confirmText = stringResource(R.string.feature_edit_create_project_confirm),
            dismissText = stringResource(R.string.feature_edit_create_project_dismiss),
            onDismissRequest = { onSelectInnerViewCreate() },
            onConfirmRequest = { addInnerProject() }
        ) {
            InnerViewDialogTextField(
                value = { editHomeUiState.dialogInnerProjectTitle },
                onValueChange = { updateDialogInnerProjectTitle(it) },
                placeholderText = stringResource(R.string.feature_edit_create_project_placeholder),
                labelText = stringResource(R.string.feature_edit_create_project_label),
                supportingText = "${editHomeUiState.dialogInnerProjectTitle.length}/$maxInnerProjectTitleLength"
            )
        }
    }
}

@Composable
private fun InnerProjectList(
    interviewGroups: ImmutableList<InnerProject>,
    deleteInnerProject: (Int) -> Unit,
    navigateToEdit: (Int) -> Unit,
) {
    val lazyGridState = rememberLazyGridState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Paddings.large)
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            state = lazyGridState,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(Paddings.medium),
            horizontalArrangement = Arrangement.spacedBy(Paddings.medium),
        ) {
            items(interviewGroups, key = { it.id }) {
                InnerProjectCard(
                    innerProject = it,
                    deleteInnerProject = deleteInnerProject,
                    navigateToEdit = navigateToEdit
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InnerProjectCard(
    modifier: Modifier = Modifier,
    innerProject: InnerProject,
    deleteInnerProject: (Int) -> Unit,
    navigateToEdit: (Int) -> Unit,
) {

    var dropDownExpanded by remember { mutableStateOf(false) }
    var deleteDialogVisible by remember { mutableStateOf(false) }

    InterviewGroupCard(
        modifier = modifier
            .height(240.dp)
            .combinedClickable(
                onClick = { navigateToEdit(innerProject.id) },
                onLongClick = { dropDownExpanded = true }
            ),
        filePath = innerProject.innerProjectComponents.media.firstOrNull()?.filePath ?: ""
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = Paddings.medium, bottom = Paddings.medium)
        ) {
            OutlinedText(
                text = innerProject.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                outlineColor = MaterialTheme.colorScheme.tertiary,
                outlineDrawStyle = Stroke(
                    width = 5f
                )
            )
            InnerViewDropdownMenu(
                modifier = Modifier,
                expanded = dropDownExpanded,
                onDismissRequest = { dropDownExpanded = false }
            ) {
                InnerViewDropdownMenuItem(
                    text = stringResource(R.string.feature_edit_inner_project_delete_confirm_text),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        deleteDialogVisible = true
                        dropDownExpanded = false
                    },
                    onDismissRequest = { dropDownExpanded = false }
                )
            }
        }

        if (deleteDialogVisible) {
            InnerViewDialog(
                titleText = stringResource(
                    R.string.feature_edit_inner_project_delete_dialog_title,
                    innerProject.title
                ),
                contentText = stringResource(
                    R.string.feature_edit_inner_project_delete_dialog_content,
                    innerProject.title
                ),
                confirmText = stringResource(R.string.feature_edit_inner_project_delete_confirm_text),
                dismissText = stringResource(R.string.feature_edit_inner_project_delete_dismiss_text),
                onDismissRequest = { deleteDialogVisible = false },
                onConfirmRequest = {
                    deleteInnerProject(innerProject.id)
                    deleteDialogVisible = false
                }
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun EditHomeContentPreview() {
    InnerViewTheme {
        EditHomeContent(
            editHomeUiState = EditHomeUiState(
                innerProjects = persistentListOf(
                    InnerProject(
                        id = 1
                    ),
                    InnerProject(
                        id = 2
                    ),
                    InnerProject(
                        id = 3
                    ),
                ),
                isInnerProjectCreateDialogVisible = false
            ),
            padding = PaddingValues(),
            onSelectInnerViewCreate = {},
            maxInnerProjectTitleLength = 0,
            updateDialogInnerProjectTitle = {},
            addInnerProject = {},
            deleteInnerProject = {},
            navigateToEdit = {}
        )
    }
}
