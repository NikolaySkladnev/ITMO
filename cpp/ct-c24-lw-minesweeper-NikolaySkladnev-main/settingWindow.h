#ifndef SETTINGSWINDOW_H
#define SETTINGSWINDOW_H

#include <QDialog>
#include <QHBoxLayout>
#include <QPushButton>
#include <QSpinBox>

class SettingsDialog : public QDialog
{
	Q_OBJECT

  public:
	SettingsDialog(QWidget *parent = nullptr);
	void addSettingRow(QVBoxLayout *mainLayout, const QString &labelText, QSpinBox *&spinBox, int min, int max, int value);
	int getRows() const;
	int getCols() const;
	int getMines() const;

  private:
	QSpinBox *rows;
	QSpinBox *cols;
	QSpinBox *mines;
	QPushButton *ok_button;
};

#endif	  // SETTINGSWINDOW_H
