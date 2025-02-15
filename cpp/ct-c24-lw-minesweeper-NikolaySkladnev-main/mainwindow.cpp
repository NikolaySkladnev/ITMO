#include "mainwindow.h"

#include "settingWindow.h"

#include <QAction>
#include <QCloseEvent>
#include <QMessageBox>
#include <QResizeEvent>
#include <QSettings>
#include <QToolBar>
#include <QVBoxLayout>

MainWindow::MainWindow(QWidget *parent, bool debugMode, bool loadFromSave) :
	QMainWindow(parent), rows(10), cols(10), mines(10), isGameOver(false), isInitialized(false), debugMode(debugMode)
{
	if (!loadFromSave)
	{
		SettingsDialog settingsDialog(this);
		if (settingsDialog.exec() == QDialog::Accepted)
		{
			rows = settingsDialog.getRows();
			cols = settingsDialog.getCols();
			mines = settingsDialog.getMines();
		}
	}

	QToolBar *toolbar = addToolBar("Toolbar");
	QAction *new_game = toolbar->addAction("New game");
	QAction *new_game_with_parameters = toolbar->addAction("New game with parameters");

	connect(new_game, &QAction::triggered, this, &MainWindow::onNewGameSameParams);
	connect(new_game_with_parameters, &QAction::triggered, this, &MainWindow::onNewGameNewParams);

	centralWidget = new QWidget(this);
	setCentralWidget(centralWidget);

	QVBoxLayout *mainLayout = new QVBoxLayout(centralWidget);

	if (debugMode)
	{
		debugCheckBox = new QCheckBox("Debug Mode", this);
		connect(debugCheckBox, &QCheckBox::toggled, this, &MainWindow::onDebugToggled);
		mainLayout->addWidget(debugCheckBox);
	}

	gridLayout = new QGridLayout();
	gridLayout->setSpacing(10);
	mainLayout->addLayout(gridLayout);

	if (!loadFromSave)
	{
		createField(rows, cols);
	}

	centralWidget->setMinimumSize(300, 300);
	resize(600, 600);

	highlightTimer = new QTimer(this);
	connect(highlightTimer, &QTimer::timeout, this, &MainWindow::resetHighlight);
}

MainWindow::~MainWindow() {}

void MainWindow::resizeEvent(QResizeEvent *event)
{
	QMainWindow::resizeEvent(event);

	int cellSize = qMin(centralWidget->width() / cols, centralWidget->height() / rows);

	for (int row = 0; row < rows; ++row)
		for (int col = 0; col < cols; ++col)
			buttons[row][col]->setFixedSize(cellSize, cellSize);

	int x = (centralWidget->width() - cellSize * cols) / 2;
	int y = (centralWidget->height() - cellSize * rows) / 2;
	gridLayout->setContentsMargins(x, y, x, y);
}

void MainWindow::resetField()
{
	isGameOver = false;

	for (int row = 0; row < buttons.size(); row++)
		for (int col = 0; col < buttons[row].size(); col++)
			delete buttons[row][col];

	buttons.clear();
	bombs.clear();
	numbers.clear();
	revealed.clear();
	flagged.clear();

	createField(rows, cols);

	updateDebugState();

	resizeEvent(new QResizeEvent(size(), size()));
}

void MainWindow::initializeField(int initialRow, int initialCol)
{
	int bombsToPlace = mines;
	while (bombsToPlace > 0)
	{
		int row = QRandomGenerator::global()->bounded(rows);
		int col = QRandomGenerator::global()->bounded(cols);
		if (!(row == initialRow && col == initialCol) && !bombs[row][col])
		{
			bombs[row][col] = true;
			bombsToPlace--;
		}
	}

	calculateNumbers();
	updateDebugState();
}

void MainWindow::calculateNumbers()
{
	for (int row = 0; row < rows; row++)
	{
		for (int col = 0; col < cols; col++)
		{
			if (bombs[row][col])
			{
				for (int i = -1; i <= 1; i++)
				{
					for (int j = -1; j <= 1; j++)
					{
						int newRow = row + i;
						int newCol = col + j;
						if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols)
						{
							numbers[newRow][newCol]++;
						}
					}
				}
			}
		}
	}
}

void MainWindow::revealCell(int row, int col)
{
	if ((row < 0 || row >= rows || col < 0 || col >= cols) || (revealed[row][col] || flagged[row][col]))
		return;

	revealed[row][col] = true;

	int number = numbers[row][col];
	buttons[row][col]->setText(QString::number(number));

	if (number == 0)
	{
		revealAdjacentCells(row, col);
	}

	checkWinCondition();
	updateDebugState();
}

void MainWindow::revealAdjacentCells(int row, int col)
{
	for (int i = -1; i <= 1; i++)
	{
		for (int j = -1; j <= 1; j++)
		{
			if (i == 0 && j == 0)
				continue;
			revealCell(row + i, col + j);
		}
	}
}

void MainWindow::onButtonClicked()
{
	if (isGameOver)
		return;

	QPushButton *button = qobject_cast< QPushButton * >(sender());
	if (!button)
		return;

	int row = -1;
	int col = -1;
	for (int i = 0; i < rows; i++)
	{
		for (int j = 0; j < cols; j++)
		{
			if (buttons[i][j] == button)
			{
				row = i;
				col = j;
				break;
			}
		}
		if (row != -1)
			break;
	}

	if (!isInitialized)
	{
		initializeField(row, col);
		isInitialized = true;
	}

	if (bombs[row][col])
	{
		button->setText("B");
		button->setStyleSheet("background-color: darkred");
		gameOver(false, row, col);
	}
	else
	{
		revealCell(row, col);
	}
}

void MainWindow::mousePressEvent(QMouseEvent *event)
{
	if (isGameOver)
		return;

	if (event->button() == Qt::RightButton)
	{
		QPushButton *button = qobject_cast< QPushButton * >(childAt(event->pos()));
		if (!button)
			return;

		int row = -1;
		int col = -1;
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				if (buttons[i][j] == button)
				{
					row = i;
					col = j;
					break;
				}
			}
			if (row != -1)
				break;
		}

		if (row != -1 && col != -1 && !revealed[row][col])
		{
			if (flagged[row][col])
			{
				button->setText("");
				flagged[row][col] = false;
			}
			else
			{
				button->setText("F");
				flagged[row][col] = true;
			}
		}
		updateDebugState();
	}
	else if (event->button() == Qt::MiddleButton)
	{
		QPushButton *button = qobject_cast< QPushButton * >(childAt(event->pos()));
		if (!button)
			return;

		int row = -1;
		int col = -1;
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				if (buttons[i][j] == button)
				{
					row = i;
					col = j;
					break;
				}
			}
			if (row != -1)
				break;
		}

		if (row != -1 && col != -1)
		{
			middleClickReveal(row, col);
		}
	}
}

void MainWindow::middleClickReveal(int row, int col)
{
	if (allFlagsCorrect(row, col))
	{
		revealAdjacentCells(row, col);
	}
	else
	{
		highlightIncorrectFlags(row, col);
	}
}

bool MainWindow::allFlagsCorrect(int row, int col)
{
	int flagCount = 0;
	int bombCount = 0;

	for (int i = -1; i <= 1; i++)
	{
		for (int j = -1; j <= 1; j++)
		{
			int newRow = row + i;
			int newCol = col + j;
			if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols)
			{
				if (flagged[newRow][newCol])
					flagCount++;
				if (bombs[newRow][newCol])
					bombCount++;
			}
		}
	}

	return flagCount == bombCount;
}

void MainWindow::highlightIncorrectFlags(int row, int col)
{
	for (int i = -1; i <= 1; i++)
	{
		for (int j = -1; j <= 1; j++)
		{
			int newRow = row + i;
			int newCol = col + j;
			if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && !revealed[newRow][newCol])
			{
				buttons[newRow][newCol]->setStyleSheet("background-color: yellow");
			}
		}
	}
	QTimer::singleShot(500, this, &MainWindow::resetHighlight);
}

void MainWindow::resetHighlight()
{
	for (int row = 0; row < rows; row++)
	{
		for (int col = 0; col < cols; col++)
		{
			if (buttons[row][col]->styleSheet() == "background-color: yellow")
			{
				buttons[row][col]->setStyleSheet("");
			}
		}
	}
	highlightTimer->stop();
}

void MainWindow::checkWinCondition()
{
	for (int row = 0; row < rows; row++)
	{
		for (int col = 0; col < cols; col++)
		{
			if (!bombs[row][col] && !revealed[row][col])
			{
				return;
			}
		}
	}
	gameOver(true);
}

void MainWindow::gameOver(bool won, int bombRow, int bombCol)
{
	if (isGameOver)
		return;

	for (int row = 0; row < rows; row++)
	{
		for (int col = 0; col < cols; col++)
		{
			if (bombs[row][col])
			{
				buttons[row][col]->setText("B");
				buttons[row][col]->setStyleSheet("background-color: red");
				if (row == bombRow && col == bombCol && !won)
					buttons[row][col]->setStyleSheet("background-color: darkred");
			}
			else
				buttons[row][col]->setText(QString::number(numbers[row][col]));
			buttons[row][col]->setEnabled(false);
		}
	}

	QString message = won ? "You won!" : "You lost!";
	QMessageBox::information(this, "Game Over", message);

	deleteSave();

	isGameOver = true;
}

void MainWindow::updateDebugState()
{
	if (isGameOver)
		return;

	for (int row = 0; row < rows; row++)
	{
		for (int col = 0; col < cols; col++)
		{
			if (debugMode && debugCheckBox->isChecked())
			{
				if (bombs[row][col])
				{
					buttons[row][col]->setText("B");
					buttons[row][col]->setStyleSheet("background-color: lightgray");
				}
				else if (flagged[row][col])
				{
					buttons[row][col]->setText("F");
					buttons[row][col]->setStyleSheet("");
				}
				else
				{
					buttons[row][col]->setText(QString::number(numbers[row][col]));
					buttons[row][col]->setStyleSheet("background-color: lightgray");
				}
			}
			else
			{
				if (!revealed[row][col])
				{
					if (flagged[row][col])
						buttons[row][col]->setText("F");

					else
					{
						buttons[row][col]->setText("");
						buttons[row][col]->setStyleSheet("");
					}
				}
				else
				{
					buttons[row][col]->setText(QString::number(numbers[row][col]));
				}
			}
			buttons[row][col]->setEnabled(true);
		}
	}
}

void MainWindow::deleteSave()
{
	QSettings settings("Minesweeper", "Game");
	settings.clear();
}

void MainWindow::closeEvent(QCloseEvent *event)
{
	saveGame();
	QMainWindow::closeEvent(event);
}

void MainWindow::onNewGameSameParams()
{
	isInitialized = false;
	resetField();
}

void MainWindow::onDebugToggled()
{
	if (isGameOver)
		return;
	updateDebugState();
}

void MainWindow::onNewGameNewParams()
{
	SettingsDialog settingsDialog(this);
	if (settingsDialog.exec() == QDialog::Accepted)
	{
		rows = settingsDialog.getRows();
		cols = settingsDialog.getCols();
		mines = settingsDialog.getMines();
	}

	isInitialized = false;
	resetField();
}

void MainWindow::saveGame()
{
	if (isGameOver)
		return;
	QSettings settings("Minesweeper", "Game");

	settings.beginGroup("GameState");
	settings.setValue("rows", rows);
	settings.setValue("cols", cols);
	settings.setValue("mines", mines);
	settings.setValue("isInitialized", isInitialized);

	for (int row = 0; row < rows; row++)
	{
		for (int col = 0; col < cols; col++)
		{
			settings.setValue(QString("bombs_%1_%2").arg(row).arg(col), bombs[row][col]);
			settings.setValue(QString("numbers_%1_%2").arg(row).arg(col), numbers[row][col]);
			settings.setValue(QString("revealed_%1_%2").arg(row).arg(col), revealed[row][col]);
			settings.setValue(QString("flagged_%1_%2").arg(row).arg(col), flagged[row][col]);
		}
	}

	settings.endGroup();
}

bool MainWindow::loadGame()
{
	QSettings settings("Minesweeper", "Game");

	if (!settings.contains("GameState/isInitialized"))
		return false;

	settings.beginGroup("GameState");
	rows = settings.value("rows", rows).toInt();
	cols = settings.value("cols", cols).toInt();
	mines = settings.value("mines", mines).toInt();
	isInitialized = settings.value("isInitialized", false).toBool();

	bombs.resize(rows);
	numbers.resize(rows);
	revealed.resize(rows);
	flagged.resize(rows);

	for (int row = 0; row < rows; row++)
	{
		bombs[row].resize(cols);
		numbers[row].resize(cols);
		revealed[row].resize(cols);
		flagged[row].resize(cols);
		for (int col = 0; col < cols; col++)
		{
			bombs[row][col] = settings.value(QString("bombs_%1_%2").arg(row).arg(col), false).toBool();
			numbers[row][col] = settings.value(QString("numbers_%1_%2").arg(row).arg(col), 0).toInt();
			revealed[row][col] = settings.value(QString("revealed_%1_%2").arg(row).arg(col), false).toBool();
			flagged[row][col] = settings.value(QString("flagged_%1_%2").arg(row).arg(col), false).toBool();
		}
	}

	settings.endGroup();

	createField(rows, cols);

	if (isInitialized)
		updateDebugState();

	return true;
}

void MainWindow::createField(int rows, int cols)
{
	buttons.resize(rows);
	bombs.resize(rows);
	numbers.resize(rows);
	revealed.resize(rows);
	flagged.resize(rows);

	gridLayout->setSpacing(10);

	for (int row = 0; row < rows; row++)
	{
		buttons[row].resize(cols);
		numbers[row].resize(cols);
		bombs[row].resize(cols);
		revealed[row].resize(cols);
		flagged[row].resize(cols);

		for (int col = 0; col < cols; col++)
		{
			buttons[row][col] = new QPushButton(this);
			buttons[row][col]->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Expanding);
			gridLayout->addWidget(buttons[row][col], row, col);
			connect(buttons[row][col], &QPushButton::clicked, this, &MainWindow::onButtonClicked);

			if (isInitialized)
			{
				if (revealed[row][col])
					buttons[row][col]->setText(QString::number(numbers[row][col]));
				if (flagged[row][col])
					buttons[row][col]->setText("F");
			}
		}
	}
	updateDebugState();
}
