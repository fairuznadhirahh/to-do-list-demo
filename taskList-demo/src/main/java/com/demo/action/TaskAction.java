package com.demo.action;

import com.demo.model.Task;
import com.demo.util.DatabaseUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TaskAction extends ActionSupport {
    private static final Logger logger = LogManager.getLogger(TaskAction.class);

    private Task task;
    private int id;
    private List<Task> tasks;
    private String errorMessage;
    private int httpStatusCode;

    public String getAll(){
        tasks = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection()){
            String sql = "SELECT * FROM tasks";

            try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){
                while (resultSet.next()){
                    Task task = new Task();
                    task.setId(resultSet.getInt("id"));
                    task.setTitle(resultSet.getString("title"));
                    task.setDescription(resultSet.getString("description"));
                    task.setStatus(resultSet.getString("status"));
                    tasks.add(task);
                }
            }
        }catch (SQLException e){
            logger.error("Database error while fetching task item", e);
            errorMessage = "Failed to retrieve Task item. Please try again";
            return ERROR;
        }
        return SUCCESS;
    }

    public String getById(){
        if (id <= 0){
            errorMessage = "Invalid input: ID must be provided";
            httpStatusCode = 400;
            return ERROR;
        }
        try (Connection connection = DatabaseUtil.getConnection()){
            String sql = "SELECT * FROM tasks WHERE id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, id);

                try (ResultSet resultSet = statement.executeQuery(sql)){
                    if (resultSet.next()){
                        Task task = new Task();
                        task.setId(resultSet.getInt("id"));
                        task.setTitle(resultSet.getString("title"));
                        task.setDescription(resultSet.getString("description"));
                        task.setStatus(resultSet.getString("status"));
                    } else {
                        errorMessage = "Task not found with ID: " + id;
                        httpStatusCode = 404;
                        return ERROR;
                    }
                }
            }

        }catch (SQLException e){
            logger.error("Database error while fetching task item", e);
            errorMessage = "Failed to retrieve Task item. Please try again";
            return ERROR;
        }
        return SUCCESS;
    }

    public String add(){
        if (task == null || task.getTitle() == null || task.getTitle().trim().isEmpty()){
            errorMessage = "Task title cannot be empty.";
            httpStatusCode = 400;
            return ERROR;
        }
        try (Connection connection = DatabaseUtil.getConnection()){
            String sql = "INSERT INTO tasks (title, description, status) VALUES (?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, task.getTitle());
                statement.setString(2, task.getDescription());
                statement.setString(3, task.getStatus());
                statement.executeUpdate();
            }
        }catch (SQLException e){
            logger.error("Database error while adding new task item", e);
            errorMessage = "Failed to add new Task item. Please try again";
            return ERROR;
        }
        return SUCCESS;
    }

    public String update(){
        if (task == null || task.getTitle() == null || task.getTitle().trim().isEmpty()){
            errorMessage = "Task description cannot be empty.";
            httpStatusCode = 400;
            return ERROR;
        }

        try (Connection connection = DatabaseUtil.getConnection()){
            String sql = "UPDATE tasks SET title=?, description=?, status=? WHERE id=?";

            try (PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, task.getTitle());
                statement.setString(2, task.getDescription());
                statement.setString(3, task.getStatus());
                statement.setInt(4, task.getId());

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated == 0){
                    errorMessage = "No task item found with ID";
                    httpStatusCode = 404;
                    return ERROR;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return SUCCESS;
    }

    public String delete(){
        if (id <= 0){
            errorMessage = "Invalid task ID provided.";
            httpStatusCode = 400;
            return ERROR;
        }
        try (Connection connection = DatabaseUtil.getConnection()){
            String sql = "DELETE FROM tasks WHERE id=?";

            try (PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, id);

                int rowDeleted = statement.executeUpdate();
                if( rowDeleted == 0){
                    errorMessage = "No task found with provided ID";
                    httpStatusCode = 404;
                    return ERROR;
                }
            }
        }catch (SQLException e){
            logger.error("Database error while deleting new task item", e);
            errorMessage = "Failed to delete Task item. Please try again";
        }
        return SUCCESS;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
