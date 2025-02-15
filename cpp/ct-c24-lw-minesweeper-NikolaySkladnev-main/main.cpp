#include "mainwindow.h"
#include "settingWindow.h"

#include <QApplication>
#include <QMessageBox>
#include <QSettings>

bool isDebugMode(int argc, char* argv[])
{
	return argc > 1 && QString(argv[1]) == "dbg";
}

bool promptLoadGame()
{
	QMessageBox msgBox;
	msgBox.setText("Загрузить сохраненную игру?");
	msgBox.setStandardButtons(QMessageBox::Yes | QMessageBox::No);
	msgBox.setDefaultButton(QMessageBox::Yes);
	return msgBox.exec() == QMessageBox::Yes;
}

MainWindow* createMainWindow(bool debugMode, bool loadFromSave)
{
	return new MainWindow(nullptr, debugMode, loadFromSave);
}

void handleNoSave(MainWindow*& w, bool debugMode)
{
	delete w;
	QMessageBox::information(nullptr, "Info", "Нет сохранений. Начните новую игру.");
	SettingsDialog settingsDialog(nullptr);
	if (settingsDialog.exec() == QDialog::Accepted)
	{
		w = createMainWindow(debugMode, false);
	}
	else
	{
		QApplication::exit(0);
	}
}

MainWindow* initializeMainWindow(bool debugMode)
{
	QSettings settings("Minesweeper", "Game");

	if (settings.contains("GameState/isInitialized") && promptLoadGame())
	{
		MainWindow* w = createMainWindow(debugMode, true);
		if (!w->loadGame())
		{
			handleNoSave(w, debugMode);
		}
		return w;
	}
	else
	{
		return createMainWindow(debugMode, false);
	}
}

int main(int argc, char* argv[])
{
	QApplication app(argc, argv);

	bool debugMode = isDebugMode(argc, argv);
	MainWindow* w = initializeMainWindow(debugMode);

	w->show();
	int result = app.exec();
	delete w;
	return result;
}
