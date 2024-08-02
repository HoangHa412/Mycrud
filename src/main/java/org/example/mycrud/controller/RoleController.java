package org.example.mycrud.controller;

import org.example.mycrud.entity.Permissions;
import org.example.mycrud.entity.Role;
import org.example.mycrud.exception.ErrorCode;
import org.example.mycrud.mapper.RoleMapper;
import org.example.mycrud.model.RoleDto;
import org.example.mycrud.model.response.BaseResponse;
import org.example.mycrud.service.PermissionService;
import org.example.mycrud.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionService permissionsService;

    @GetMapping
    public ResponseEntity<?> getAllRoles(){
        List<Role> roles = roleService.getAll();

        List<RoleDto> roleDto = new ArrayList<>();
        for(Role role: roles){
            roleDto.add(roleMapper.toRoleDto(role));
        }
        return ResponseEntity.ok()
                .body(BaseResponse.builder().code(ErrorCode.SUCCESS.getCode()).message(ErrorCode.SUCCESS.getMessage()).content(roleDto).build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer id){
        if(roleService.findById(id) != null){
            roleService.deleteRoleById(id);
            return ok(new BaseResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), null));
        }else{
            return ok(BaseResponse.builder().code(ErrorCode.USER_NOT_FOUND.getCode()).message(ErrorCode.USER_NOT_FOUND.getMessage()).build());
        }
    }

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody RoleDto roleDto){
        Role role = roleMapper.toRole(roleDto);
        roleService.createRole(role);
        return ResponseEntity.ok()
                .body(BaseResponse.builder().code(ErrorCode.SUCCESS.getCode()).message(ErrorCode.SUCCESS.getMessage()).content(role).build());
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateRole(@RequestBody RoleDto roleDto,@PathVariable Integer id){
        Role roleOptional = roleService.findById(id);
        if(roleOptional != null){
            roleOptional.setName(roleDto.getName());
            Set<Permissions> permissions = new HashSet<>();
            roleDto.getPermissions().forEach(permissionsName ->{
                Optional<Permissions> permission = permissionsService.getPermissionsByName(permissionsName);
                permission.ifPresent(permissions::add);
            });
            roleOptional.setPermissions(permissions);
            roleService.createRole(roleOptional);
        }
            return ResponseEntity.ok()
                   .body(BaseResponse.builder().code(ErrorCode.SUCCESS.getCode()).message(ErrorCode.SUCCESS.getMessage()).content(roleOptional).build());
    }
}
