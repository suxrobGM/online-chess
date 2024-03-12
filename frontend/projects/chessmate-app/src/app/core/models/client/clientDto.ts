import {UserDto} from '../userDto';

export interface ClientDto {
  id: string;
  name: string;
  user?: UserDto;
  createdDate: Date;
}
