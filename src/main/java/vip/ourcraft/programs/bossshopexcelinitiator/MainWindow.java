package vip.ourcraft.programs.bossshopexcelinitiator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.*;
import java.util.HashMap;
import java.util.List;

public class MainWindow {
    private JFrame frame;
    private JTextArea sourceFilePathsTextArea;
    private JPanel panel;
    private JButton runButton;
    private JTextField languageFilePathTextField;

    MainWindow() {
        // 文件路径接收器
        new DropTarget(languageFilePathTextField, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    // 接收拖拽来的数据
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    try {
                        @SuppressWarnings("unchecked")
                        List<File> files = (List<File>) (event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));

                        languageFilePathTextField.setText(files.get(0).getAbsolutePath());
                        event.dropComplete(true);
                    } catch (UnsupportedFlavorException | IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                event.rejectDrop();
            }
        });
        
        // 文件路径接收器
        new DropTarget(sourceFilePathsTextArea, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    // 接收拖拽来的数据
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    try {
                        @SuppressWarnings("unchecked")
                        List<File> files = (List<File>) (event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));

                        for (File file : files) {
                            if (!sourceFilePathsTextArea.getText().equals("")) {
                                sourceFilePathsTextArea.append("\n");
                            }

                            sourceFilePathsTextArea.append(file.getAbsolutePath());
                        }

                        event.dropComplete(true);
                    } catch (UnsupportedFlavorException | IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                event.rejectDrop();
            }
        });

        runButton.addActionListener(e -> {
            run();
        });
    }

    void run() {
        if (languageFilePathTextField.getText().equals("")) {
            sendWarningMsgBox("语言文件路径不能为空!");
            return;
        }

        if (sourceFilePathsTextArea.getText().equals("")) {
            sendWarningMsgBox("源文件路径不能为空!");
            return;
        }

        File languageFile = new File(languageFilePathTextField.getText());

        if (!languageFile.exists()) {
            sendWarningMsgBox("语言文件不存在!");
            return;
        }

        String lines = Util.readFile(languageFile);

        if (lines == null) {
            sendWarningMsgBox("语言文件为空!");
            return;
        }

        HashMap<String, MinecraftItem> mcItems = new HashMap<>();
        boolean isFirstLine = true;

        // 存储汉化信息
        for (String line : lines.split(Util.LINE_SEPARATOR)) {
            if (!isFirstLine) {
                String[] lineArr = line.split(",");

                mcItems.put(lineArr[3], new MinecraftItem(lineArr[3], lineArr[2], Integer.parseInt(lineArr[0]), Integer.parseInt(lineArr[1])));
            } else {
                isFirstLine = false;
            }
        }

        // 遍历读取并处理excel文件
        for (String path : sourceFilePathsTextArea.getText().split("\n")) {
            File sourceFile = new File(path);

            if (!sourceFile.exists()) {
                sendWarningMsgBox("Excel文件不存在: " + sourceFile.getAbsolutePath());
                continue;
            }

            Workbook workbook;

            try {
                workbook = new XSSFWorkbook(new FileInputStream(sourceFile));
            } catch (IOException e1) {
                e1.printStackTrace();
                sendErrorMsgBox(e1.getLocalizedMessage());
                continue;
            }


            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                int counter = 2;

                // 填写物品索引
                for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);

                    if (row == null) {
                        continue;
                    }

                    Cell cell1 = row.getCell(0);

                    if (cell1 == null) {
                        cell1 = row.createCell(0);
                    }

                    counter += counter % 9 == 0 ? 3 : 1;

                    cell1.setCellValue(counter);
                }

                // 填写物品信息
                for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);

                    if (row == null) {
                        continue;
                    }

                    Cell chineseNameCell = row.getCell(1);

                    if (chineseNameCell == null) {
                        continue;
                    }

                    MinecraftItem mcItem = mcItems.get(String.valueOf(chineseNameCell));

                    if (mcItem != null) {
                        row.createCell(2);
                        row.createCell(3);

                        Cell englishNameCell = row.getCell(2);
                        Cell durabilityCell = row.getCell(3);

                        if (englishNameCell.getStringCellValue().equals("") && durabilityCell.getStringCellValue().equals("")) {
                            englishNameCell.setCellValue(mcItem.getEnglishName());
                            durabilityCell.setCellValue(mcItem.getDurability());
                        }

                    }
                }
            }

            try {
                workbook.write(new FileOutputStream(sourceFile));
            } catch (IOException e1) {
                e1.printStackTrace();
                sendErrorMsgBox(e1.getLocalizedMessage());
                return;
            }

            sendInfoMsgBox("更新文件成功!");
        }
    }

    void init() {
        this.frame = new JFrame("BossShopExcelInitiator");

        frame.setContentPane(new MainWindow().panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        frame.setSize(500,200);
    }

    private void sendInfoMsgBox(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "警告", JOptionPane.INFORMATION_MESSAGE);
    }

    private void sendWarningMsgBox(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "警告", JOptionPane.WARNING_MESSAGE);
    }

    private void sendErrorMsgBox(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "错误", JOptionPane.ERROR_MESSAGE);
    }
}
