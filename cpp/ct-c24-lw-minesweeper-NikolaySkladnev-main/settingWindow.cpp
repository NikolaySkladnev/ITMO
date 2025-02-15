#include "settingWindow.h"

#include <QHBoxLayout>
#include <QLabel>
#include <QVBoxLayout>

void SettingsDialog::addSettingRow(QVBoxLayout *mainLayout, const QString &labelText, QSpinBox *&qSpinBox, int min, int max, int value)
{
	QHBoxLayout *layout = new QHBoxLayout();
	QLabel *qlabel = new QLabel(labelText, this);
	qSpinBox = new QSpinBox(this);
	qSpinBox->setRange(min, max);
	qSpinBox->setValue(value);
	layout->addWidget(qlabel);
	layout->addWidget(qSpinBox);
	mainLayout->addLayout(layout);
}

SettingsDialog::SettingsDialog(QWidget *parent) : QDialog(parent)
{
	setWindowTitle("Настройки");

	QVBoxLayout *mainLayout = new QVBoxLayout(this);

	addSettingRow(mainLayout, "Длина:", rows, 2, 50, 10);
	addSettingRow(mainLayout, "Ширина:", cols, 2, 50, 10);
	addSettingRow(mainLayout, "Количество мин:", mines, 1, 100, 10);

	ok_button = new QPushButton("OK", this);
	connect(ok_button, &QPushButton::clicked, this, &QDialog::accept);

	mainLayout->addWidget(ok_button);

	setLayout(mainLayout);
	setFixedSize(275, 300);
}

int SettingsDialog::getRows() const
{
	return rows->value();
}

int SettingsDialog::getCols() const
{
	return cols->value();
}

int SettingsDialog::getMines() const
{
	return mines->value();
}
