#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QCheckBox>
#include <QGridLayout>
#include <QMainWindow>
#include <QMouseEvent>
#include <QPushButton>
#include <QRandomGenerator>
#include <QSettings>
#include <QTimer>
#include <QToolBar>
#include <QVector>

class MainWindow : public QMainWindow
{
	Q_OBJECT

  public:
	MainWindow(QWidget *parent = nullptr, bool debugMode = false, bool loadFromSave = false);
	~MainWindow();

	bool loadGame();

  protected:
	void closeEvent(QCloseEvent *event) override;
	void mousePressEvent(QMouseEvent *event) override;
	void resizeEvent(QResizeEvent *event) override;

  private slots:
	void onButtonClicked();
	void onDebugToggled();
	void onNewGameSameParams();
	void onNewGameNewParams();
	void resetHighlight();

  private:
	int rows;
	int cols;
	int mines;
	bool isGameOver;
	bool isInitialized;
	bool debugMode;

	QCheckBox *debugCheckBox;
	QWidget *centralWidget;
	QGridLayout *gridLayout;
	QVector< QVector< QPushButton * > > buttons;
	QVector< QVector< bool > > bombs;
	QVector< QVector< int > > numbers;
	QVector< QVector< bool > > revealed;
	QVector< QVector< bool > > flagged;
	QTimer *highlightTimer;

	void saveGame();
	void resetField();
	void deleteSave();
	void updateDebugState();
	void calculateNumbers();
	void checkWinCondition();
	void revealCell(int row, int col);
	bool allFlagsCorrect(int row, int col);
	void middleClickReveal(int row, int col);
	void revealAdjacentCells(int row, int col);
	void highlightIncorrectFlags(int row, int col);
	void createField(int rows, int cols);
	void initializeField(int initialRow, int initialCol);
	void gameOver(bool won, int bombRow = -1, int bombCol = -1);
};

#endif	  // MAINWINDOW_H
