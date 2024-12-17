package com.dev.innerview.feature.edit

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.innerview.core.designsystem.component.InnerViewDialog
import com.dev.innerview.core.designsystem.component.InnerViewDialogTextField
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
    navigateToEdit: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        InnerViewTopAppBar(
            title = "프로젝트 편집"
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
                    navigateToEdit = navigateToEdit
                )
                InnerViewFloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = Paddings.large, bottom = Paddings.large),
                    iconImageVector = Icons.Filled.Add,
                    text = "새로운 프로젝트",
                    onClick = { onSelectInnerViewCreate() }
                )
            }
        }
    }

    if (editHomeUiState.isInnerProjectCreateDialogVisible) {
        InnerViewDialog(
            titleText = "프로젝트 생성",
            contentText = "새로운 프로젝트의 제목을 지정해주세요.",
            confirmText = "생성",
            dismissText = "취소",
            onDismissRequest = { onSelectInnerViewCreate() },
            onConfirmRequest = { addInnerProject() }
        ) {
            InnerViewDialogTextField(
                value = { editHomeUiState.dialogInnerProjectTitle },
                onValueChange = { updateDialogInnerProjectTitle(it) },
                placeholderText = "프로젝트 제목을 입력하세요.",
                labelText = "제목",
                supportingText = "${editHomeUiState.dialogInnerProjectTitle.length}/$maxInnerProjectTitleLength"
            )
        }
    }
}

@Composable
private fun InnerProjectList(
    interviewGroups: ImmutableList<InnerProject>,
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
                InterviewGroupCard(
                    modifier = Modifier
                        .height(240.dp)
                        .clickable { navigateToEdit(it.id) },
                    filePath = it.innerProjectComponents.videos.firstOrNull()?.filePath ?: ""
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = Paddings.medium, bottom = Paddings.medium)
                    ) {
                        OutlinedText(
                            text = it.title ?: "empty",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onTertiary
                            ),
                            outlineColor = MaterialTheme.colorScheme.tertiary,
                            outlineDrawStyle = Stroke(
                                width = 5f
                            )
                        )
                    }
                }
            }
            repeat(interviewGroups.size % 2 + 1) {
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
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
            navigateToEdit = {}
        )
    }
}
